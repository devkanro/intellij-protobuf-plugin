---
tags: [architecture, overview, layers]
related: [architecture/extension-points, design/psi-mixin-injection]
refs: [docs/architecture.md, docs/overview.md]
---
# Plugin Architecture Layers

IntelliJ Protobuf Language Plugin is a full-featured Protocol Buffers language plugin for JetBrains IDEs with deep semantic understanding, cross-file symbol resolution, multi-language integration, and Editions support.

## Layer Diagram

```
┌─────────────────────────────────────────────┐
│             UI & Settings                   │
│   Settings, Structure View, Icons, Actions  │
├─────────────────────────────────────────────┤
│           Language Support                  │
│   Completion, Formatting, Annotations,      │
│   References, Quick Fixes, Find Usages      │
├─────────────────────────────────────────────┤
│         PSI (Program Structure)             │
│   Elements, Mixins, Scope, Features         │
├─────────────────────────────────────────────┤
│           Indexing & Stubs                  │
│   Stub Indices, Root Providers, Caching     │
├─────────────────────────────────────────────┤
│           Parsing & Lexing                  │
│   Lexer (FLEX), Parser (BNF), Language Def  │
├─────────────────────────────────────────────┤
│       Integration Modules (Optional)        │
│   Java, Go, Sisyphus, gRPC, AIP            │
└─────────────────────────────────────────────┘
```

Each layer depends only on the layer below it. Integration modules sit alongside, hooking into specific layers via extension points.

## Key Concepts

- **Proto Files as First-Class Code**: `.proto` files get full PSI trees, stub indices, and cross-project reference resolution like Java or Kotlin.
- **Multi-Source Symbol Resolution**: Root providers aggregate proto files from project sources, library JARs, the protobuf SDK, and decompiled descriptors.
- **Integration Modules**: Language-specific features activate only when relevant IDE plugins are present, keeping the core lightweight.
- **Internal Compiler**: In-process protobuf compiler produces `FileDescriptorProto` from PSI without external `protoc`.

## Parsing & Lexing

Grammar files in `src/main/grammar/` define the syntax — `protobuf.bnf` for proto2/proto3/editions, `prototext.bnf` for text format. Grammar-Kit and JFlex generate the parser and lexer. Generated code goes to `build/generated/sources/grammar/`.
