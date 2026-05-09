---
tags: [design, prototext, textproto, language]
related: [design/dual-psi-hierarchies, design/prototext-schema-linking]
refs: [docs/design/prototext.md]
---
# ProtoText — Separate Language

ProtoText (`.textproto` / `.txtpb`) is a completely independent IntelliJ language with its own grammar (`prototext.bnf`, ~200 lines vs proto's ~600), lexer, PSI tree hierarchy, and token types.

## Why Not Reuse Proto Parser?

- **Grammar structure**: Proto top-level is `(Edition|Syntax)? (Import|Package|Option|Message|Enum|Service)*`. TextProto is just `Field*`.
- **Token types**: TextProto uses `#` for comments; proto uses `//` and `/* */`.
- **PSI semantics**: A `FieldName` in proto is a declaration site. In textproto it's a usage site that must resolve to a proto declaration. Same name, opposite roles.

## Three Reference Types

| Reference | Source | Resolves To |
|---|---|---|
| `ProtoTextFieldReference` | Field name in assignment | `ProtobufFieldLike` in the message schema |
| `ProtoTextTypeNameReference` | Extension/any type bracket `[pkg.Type]` | Field definition or global type |
| `ProtoTextEnumValueReference` | Enum literal value | `ProtobufEnumValueDefinition` |

Field references walk up the textproto tree to find the owner message context, then search that message's items. Map field `key`/`value` handled specially.

## Core Design Tension

TextProto is syntactically independent but semantically dependent on proto. The architecture makes languages fully separate (different grammars, lexers, PSI trees) while making the reference system fully integrated (textproto references resolve to proto PSI nodes using shared symbol resolution).
