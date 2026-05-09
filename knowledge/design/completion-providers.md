---
tags: [design, completion, code-completion]
related: [design/completion-insert-handlers, architecture/extension-points]
refs: [docs/design/code-completion.md]
---
# Code Completion — Provider-Per-Context

Protobuf completion is fundamentally context-dependent. What's valid depends on cursor position: after `message Foo {` → field types/keywords; inside a type → scalars and user types; at a field name → names derived from type.

## Provider-Per-Context Model

Each context gets its own `CompletionProvider` registered against a PSI pattern in `ProtobufCompletionContributor`:

| Provider | Fires When | Suggests |
|---|---|---|
| `KeywordsProvider` | Scope-sensitive (top-level, message, enum, service) | Keywords valid at nesting depth |
| `BuiltInTypeProvider` | Inside `ProtobufTypeName`, not after `.` | `int32`, `string`, `bool`, etc. |
| `SyntaxProvider` | Inside `ProtobufSyntaxStatement` string | `proto2`, `proto3` |
| `FieldNameProvider` | Field name position | Names derived from field's type |
| `EnumValueNameProvider` | Enum value name position | `ENUM_UNSPECIFIED`, uppercase parent name |
| `AipMethodCompletion` | Inside RPC identifier | Standard AIP method prefixes |
| `AipResourceCompletion` | After an AIP method prefix | Full RPC signatures |

## Convention-Aware Name Generation

`FieldNameProvider` derives suggestions from the schema:
- Well-known types get special treatment: `FieldMask` → `mask`, `Timestamp` → `time`
- User types become snake_case field names via word splitting
- `repeated` fields get pluralized names (`repeated User` → `users`)

`EnumValueNameProvider` converts the parent enum name to `SCREAMING_SNAKE_CASE` and suggests `<ENUM>_UNSPECIFIED = 0;`.
