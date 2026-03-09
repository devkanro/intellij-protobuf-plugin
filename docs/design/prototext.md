# ProtoText Support

## The Problem

Protobuf has two file formats that look superficially similar but are fundamentally
different languages:

- **`.proto` files** — schema definitions (declare structure: messages, fields, types)
- **`.textproto` / `.txtpb` files** — data instances (assign values to fields of a message)

They share vocabulary (field names, type names, enum values) but differ in syntax, purpose,
and — critically — in how meaning is determined. A `.proto` file is self-contained: its
meaning comes from its own declarations. A `.textproto` file is **meaningless without a
schema** — you can't validate field names, check types, or resolve enums unless you know
which message type the file represents.

Reusing the proto parser would mean forcing an instance format into a definition grammar.
The grammars are structurally incompatible: proto has `message Foo { int32 bar = 1; }`
while textproto has `bar: 42`. Shared parsing would require so many special cases that the
"shared" code would be harder to maintain than two separate implementations.

## Design Decisions

### Decision 1: Separate Language, Shared Concepts

**Choice:** ProtoText is a completely independent IntelliJ language with its own grammar
(`prototext.bnf`, ~200 lines vs proto's ~600), its own lexer, its own PSI tree hierarchy
(`ProtoTextElement` vs `ProtobufElement`), and its own token types (notably `#` comments
instead of `//` and `/* */`).

**What would sharing have broken?**

- **Grammar structure.** Proto's top-level is `(Edition|Syntax)? (Import|Package|Option|Message|Enum|Service)*`. TextProto's top-level is just `Field*`. Sharing a grammar would mean the textproto parser carries dead weight for every proto construct.
- **Token types.** TextProto uses `#` for comments; proto uses `//` and `/* */`. Sharing a lexer would mean conditional comment handling everywhere.
- **PSI semantics.** A `FieldName` in proto is a declaration site. A `FieldName` in textproto is a usage site that must resolve to a proto declaration. Same name, opposite roles — sharing the class would conflate definition and reference.

**What IS shared:** The reference resolution infrastructure. TextProto references resolve
*to* proto PSI classes (`ProtobufFieldDefinition`, `ProtobufEnumValueDefinition`,
`ProtobufMessageDefinition`). Both languages use `ProtobufSymbolResolver` for scope-based
lookup and the same stub indexes (`ShortNameIndex`, `QualifiedNameIndex`) for cross-file
resolution. The separation is at the language boundary; the integration is at the semantic
boundary.

### Decision 2: Schema Linking via Header Comments

**Choice:** TextProto files declare their schema using special comments at the top of the
file:

```
# proto-file: path/to/schema.proto
# proto-message: package.MessageName
```

These comments are parsed by `ProtoTextSharpCommentReferenceContributor` and create
real IntelliJ references:

- `ProtoTextHeaderFileReference` resolves `# proto-file:` to a `ProtobufFile`
  (supports both relative paths and module paths via `ProtobufRootResolver`)
- `ProtoTextHeaderMessageReference` resolves `# proto-message:` within the resolved
  file's scope to a `ProtobufMessageDefinition`

**Why comments instead of a language construct?** Because the text format specification
doesn't define a schema-linking syntax. These header comments are a de facto convention
(used by Google's internal tools and `buf`), not part of the grammar. Making them comments
means:

- Files remain valid textproto regardless of tooling
- The linking mechanism is optional — files without headers just lose IDE features
- The `ProtoTextFile.schema()` method returns `null` gracefully when headers are missing

**What alternatives exist?** External configuration (a project-level mapping file) or
filename conventions. Both are more fragile and less discoverable than inline comments.

### Decision 3: Asymmetric Reference System

**Choice:** TextProto references always point *outward* to proto definitions. Proto never
references textproto. This creates a one-directional dependency graph.

The three reference types in textproto are:

| Reference | Source | Resolves To |
|---|---|---|
| `ProtoTextFieldReference` | Field name in assignment | `ProtobufFieldLike` in the message schema |
| `ProtoTextTypeNameReference` | Extension/any type bracket `[pkg.Type]` | `ProtobufFieldDefinition` or global type via stub index |
| `ProtoTextEnumValueReference` | Enum literal value | `ProtobufEnumValueDefinition` in the field's enum type |

**Resolution strategy:** Field references walk up the textproto tree to find the owner
message context, then search that message's items for a matching field name. For map fields,
the synthetic `key` and `value` fields are handled specially. Type references for extensions
use `ProtobufSymbolResolver`; for `Any` types, they use the global `QualifiedNameIndex`.

Completion leverages the same resolution: knowing the current message context lets the
plugin suggest all valid field names (with `" {}"` for message fields, `": "` for scalars)
and all valid enum values.

## How It Fits Together

```
.textproto file
    ↓
Parse with prototext grammar → ProtoTextFile PSI tree
    ↓
Read header comments:
    # proto-file: → ProtoTextHeaderFileReference → ProtobufFile
    # proto-message: → ProtoTextHeaderMessageReference → ProtobufMessageDefinition
    ↓
For each field assignment:
    FieldName → ProtoTextFieldReference → ProtobufFieldLike
        If field type is message → recurse into nested message context
        If field type is enum → ProtoTextEnumValueReference → ProtobufEnumValueDefinition
        If extension syntax [Type] → ProtoTextTypeNameReference → resolve globally
```

The schema acts as the "type system" for textproto. Without it, field names are just
strings. With it, the plugin provides completion, validation, navigation, and rename
refactoring — all flowing from the two-line header comment.

## Key Insight

The core design tension is: textproto is syntactically independent but semantically
dependent on proto. The architecture resolves this by making the *languages* fully separate
(different grammars, lexers, PSI trees) while making the *reference system* fully integrated
(textproto references resolve to proto PSI nodes, using the same symbol resolution
infrastructure). This means each language can evolve its parsing independently, but IDE
features like go-to-definition and completion work seamlessly across the boundary.
