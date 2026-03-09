# Code Completion

## The Problem

Protobuf completion is fundamentally **context-dependent** in ways that keyword completion
in a general-purpose language is not. What's valid to type depends entirely on *where* the
cursor is:

- After `message Foo {` → field types, keywords (`repeated`, `optional`, `oneof`), nested definitions
- Inside a type position → built-in scalars (`int32`, `string`) and user-defined message/enum types
- At a field name position → names derived from the field's type (a `User` field → `user`)
- At an enum value name position → `SCREAMING_SNAKE_CASE` values following proto conventions
- Inside `syntax = "` → exactly `"proto2"` or `"proto3"`

A single monolithic completion handler would drown in conditional branches. Worse, the
*insert behavior* differs per context: completing a type should add a space, completing a
field name should insert `= <number>;`, completing an import should add the import statement.

## Design Decisions

### Decision 1: Provider-Per-Context Model

**Choice:** Each completion context gets its own `CompletionProvider` registered against a
PSI pattern, all wired together by `ProtobufCompletionContributor`.

**Why not one big handler?** Because suggestion logic and insert behavior are coupled *per
context*, not per file. `KeywordsProvider` knows that `import` needs `" \"\""` appended.
`FieldNameProvider` knows it must auto-increment field numbers. Mixing these into one class
creates a maintenance nightmare where every change risks breaking unrelated contexts.

**Why pattern matching?** IntelliJ's `PlatformPatterns` API lets each provider declare
*exactly* where it fires — `withParent(PsiErrorElement).inside(ProtobufEnumBody)` for enum
scope, `withSuperParent(2, ProtobufFile)` for top-level. This moves context detection out
of provider code and into declarative registration, making it obvious which provider owns
which position.

The current providers are:

| Provider | Fires When | Suggests |
|---|---|---|
| `KeywordsProvider` | Scope-sensitive (top-level, message, enum, service, method) | Keywords valid at that nesting depth |
| `BuiltInTypeProvider` | Inside `ProtobufTypeName`, not after `.` | `int32`, `string`, `bool`, etc. |
| `SyntaxProvider` | Inside `ProtobufSyntaxStatement` string | `proto2`, `proto3` |
| `FieldNameProvider` | Field name position in a field definition | Names derived from the field's type |
| `EnumValueNameProvider` | Enum value name position | `ENUM_UNSPECIFIED`, uppercase parent name |
| `AipMethodCompletion` | Inside RPC identifier | Standard AIP method prefixes (`Get`, `List`, `Create`...) |
| `AipResourceCompletion` | After an AIP method prefix | Full RPC signatures with resource names |

### Decision 2: Smart Insert Handlers

**Choice:** Each lookup element carries a custom `InsertHandler` that transforms the
document *beyond* just inserting text — adding closing delimiters, positioning the cursor,
auto-incrementing field numbers, adding imports, and triggering follow-up completion.

**Why this matters:** In protobuf, completing a keyword is rarely the end of the
interaction. When a user completes `import`, they want the cursor between quotes. When they
complete a field name, they want `= N;` with the next available field number. When they
complete an AIP method prefix like `Get`, they want a follow-up popup offering resource
names.

Key insert handler classes:

- **`SmartInsertHandler`** — Core handler that inserts text at a given offset, avoids
  duplicating text already present (via `commonPrefixWith`), and optionally triggers
  follow-up completion
- **`AddImportInsertHandler`** — Adds an import statement when completing a cross-file type
- **`AutoPopupInsertHandler`** — Triggers `autoPopupMemberLookup` to chain completions
- **`ComposedInsertHandler`** — Sequences multiple handlers for complex insertions

### Decision 3: Convention-Aware Name Generation

**Choice:** `FieldNameProvider` and `EnumValueNameProvider` don't just offer static lists —
they *derive* suggestions from the proto schema using naming conventions.

`FieldNameProvider` examines the field's type and generates contextual names:
- Well-known types get special treatment: `FieldMask` → `mask`, `Timestamp` → `time`
- User-defined types become snake_case field names via word splitting
- `repeated` fields get pluralized names (a `repeated User` → `users`)
- The insert handler finds the previous field number and offers `= <next>;`

`EnumValueNameProvider` converts the parent enum name to `SCREAMING_SNAKE_CASE` and
suggests `<ENUM>_UNSPECIFIED = 0;` as the first value (a proto3 convention).

This means completion teaches users protobuf conventions as they type, not just what's
syntactically valid.

## How It Fits Together

```
User types in .proto file
    ↓
ProtobufCompletionContributor matches cursor position against PSI patterns
    ↓
Matching provider generates LookupElements with tailored InsertHandlers
    ↓
User selects a suggestion
    ↓
InsertHandler fires: inserts text + adjusts cursor + adds imports + triggers follow-up popup
```

The AIP completion system (`AipCompletionContributor`) is registered as a separate
contributor and demonstrates how this architecture extends: it adds Google API design
pattern suggestions without modifying the core completion logic.

## Key Insight

The completion system's power comes from treating each context as a *workflow*, not just a
word lookup. Completing a field name isn't "insert text" — it's "insert name, assign next
field number, terminate with semicolon, position cursor." The provider-per-context model
makes these workflows composable and independently testable, while the smart insert handler
chain means each step of the workflow can be mixed and matched.
