---
tags: [design, prototext, schema, references]
related: [design/prototext-separate-language, design/root-providers]
refs: [docs/design/prototext.md]
---
# ProtoText Schema Linking

TextProto files declare their schema using header comments:

```
# proto-file: path/to/schema.proto
# proto-message: package.MessageName
```

## How It Works

Parsed by `ProtoTextSharpCommentReferenceContributor`, creating real IntelliJ references:
- `ProtoTextHeaderFileReference` resolves `# proto-file:` to a `ProtobufFile` (supports relative and module paths via `ProtobufRootResolver`)
- `ProtoTextHeaderMessageReference` resolves `# proto-message:` within the resolved file's scope to a `ProtobufMessageDefinition`

## Why Comments?

The text format spec doesn't define a schema-linking syntax. These header comments are a de facto convention (used by Google's internal tools and `buf`). Making them comments means:
- Files remain valid textproto regardless of tooling
- The linking mechanism is optional — files without headers just lose IDE features
- `ProtoTextFile.schema()` returns `null` gracefully when headers are missing

## What Schema Enables

The schema acts as the "type system" for textproto. Without it, field names are just strings. With it: completion, validation, navigation, and rename refactoring — all flowing from the two-line header comment.
