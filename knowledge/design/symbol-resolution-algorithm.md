---
tags: [design, symbol-resolution, imports]
related: [design/scope-hierarchy, design/root-providers]
refs: [docs/design/symbol-resolution.md]
---
# Symbol Resolution Algorithm

## Absolute vs Relative

A leading dot (`.google.protobuf.Timestamp`) forces absolute lookup from the root. An unqualified name (`Timestamp`, `inner.Msg`) triggers relative resolution. These are separate code paths:

- **Absolute** — match the fully-qualified name against every file's package + definition tree. No scope walking.
- **Relative** — walk up the enclosing scope chain first (`resolveInCurrent`), then compose with the file's package and search imports.

## Relative Resolution Flow

1. Walk up enclosing scope chain, checking each scope for a matching child
2. If not found locally, compose the relative name with the file's package
3. Push all imported files onto a stack and repeat relative composition for each
4. Follow public imports transitively; regular imports are single-hop only

## Import Graph Traversal

- **Regular imports** — symbols available only to the importing file. Their own imports are *not* followed.
- **Public imports** — symbols re-exported. Resolver pushes their `public` imports onto the stack for further traversal.
- A single `if (it.public())` guard implements the full transitivity rule from the protobuf spec.

## Context-Aware Filters

Resolution accepts a `PsiElementFilter` that rejects invalid matches. Each reference type supplies its own filter:
- `ProtobufTypeNameReference` — must be message or enum
- `ProtobufFieldReference` — must be a field
- `ProtobufExtensionFieldReference` — must belong to a specific target message

This keeps the resolver generic while pushing validation to the call site.
