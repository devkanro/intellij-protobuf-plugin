# Architecture

A high-level map of how the plugin is structured. For the *why* behind each subsystem, see the [design documents](design/).

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

## Parsing & Lexing

Transforms `.proto` source text into a structured AST. Grammar files in `src/main/grammar/` define the syntax — `protobuf.bnf` for proto2/proto3/editions, `prototext.bnf` for text format. Grammar-Kit and JFlex generate the parser and lexer.

→ Design: [ProtoText](design/prototext.md), [Editions](design/editions.md)

## PSI Layer

The parsed AST becomes a tree of typed PSI elements. Behavior is injected via *mixins* (not inheritance) so generated classes stay untouched. *Feature interfaces* define cross-cutting capabilities like naming, scoping, and reference resolution.

→ Design: [PSI & Mixin Pattern](design/psi-and-mixin.md)

## Indexing & Stubs

Stubs are lightweight serialized snapshots of PSI elements that enable fast symbol lookup without parsing every file. Three indices (`ShortName`, `QualifiedName`, `ResourceType`) cover the common lookup patterns. Root providers aggregate proto files from project sources, libraries, SDKs, and decompiled descriptors.

→ Design: [Stub Indexing](design/stub-indexing.md)

## Language Support

The IDE features users interact with: completion, annotations, formatting, references, quick fixes. Each feature follows IntelliJ's extension model but with design choices specific to protobuf's needs.

→ Design: [Code Completion](design/code-completion.md), [Annotation System](design/annotation-system.md), [Symbol Resolution](design/symbol-resolution.md)

## Internal Compiler

The plugin includes an in-process protobuf compiler that generates `FileDescriptorProto` from PSI — without calling external `protoc`. This enables IDE features that need descriptor information (like option validation) to work without build tool configuration.

→ Design: [Compiler System](design/compiler-system.md)

## Integration Modules

Optional modules activate when their IDE dependencies are present (e.g., Java module loads only if `com.intellij.modules.java` is available). Each module extends the core via extension points to add language-specific navigation, decompilation, or code generation features.

→ Docs: [Java](modules/java.md), [Go](modules/go.md), [gRPC](modules/grpc.md), [AIP](modules/aip.md), [Sisyphus](modules/sisyphus.md)

## Extension Points

Five extension points allow third-party plugins to extend the core:

| Extension Point | What It Enables |
|-----------------|-----------------|
| `rootProvider` | Add custom proto file search locations |
| `symbolReferenceProvider` | Add custom symbol resolution strategies |
| `indexProvider` | Contribute additional data to stub indices |
| `stubExternalProvider` | Attach external metadata to stubs |
| `protocPlugin` | Extend the internal compiler |

→ Docs: [Extension Points](extension-points.md)