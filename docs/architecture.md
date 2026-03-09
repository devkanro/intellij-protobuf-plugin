# Architecture

This document describes the high-level architecture of the IntelliJ Protobuf Plugin.

## Layered Architecture

The plugin follows a multi-layered modular design. Each layer depends only on the layer below it.

```
+---------------------------------------------+
|           UI & Settings Layer               |
|  Settings, Structure View, Icons, Actions   |
+---------------------------------------------+
|          Language Support Layer              |
|  Highlighting, Completion, Formatting,      |
|  References, Annotations, Quick Fixes       |
+---------------------------------------------+
|          PSI (Program Structure) Layer       |
|  Elements, Mixins, Scope, References        |
+---------------------------------------------+
|          Indexing & Stub Layer               |
|  Stub Indices, Root Providers, Caching      |
+---------------------------------------------+
|          Parsing & Lexing Layer              |
|  Lexer (FLEX), Parser (BNF), Language Def   |
+---------------------------------------------+
|       Integration Modules (Optional)        |
|  Java, Go, Sisyphus, gRPC, AIP             |
+---------------------------------------------+
```

## Parsing & Lexing Layer

The foundation. Transforms `.proto` source text into a structured AST.

- **Grammar files** in `src/main/grammar/` define the language syntax
  - `protobuf.bnf` — BNF grammar for proto2/proto3, generates parser and PSI classes
  - `protobuf.flex` — FLEX lexer definition, generates tokenizer
  - `prototext.bnf` — BNF grammar for proto text format (`.textproto`, `.pbtxt`)
- **Language definitions** register the file types with IntelliJ
  - `ProtobufLanguage` / `ProtoTextLanguage` — language singletons
  - `ProtobufFileType` / `ProtoTextFileType` — file type associations
  - `ProtobufParserDefinition` / `ProtoTextParserDefinition` — parser entry points

**Code**: `lang/ProtobufLanguage.kt`, `lang/ProtobufFileType.kt`, `lang/ProtobufParserDefinition.kt`

## PSI Layer

The Program Structure Interface represents the parsed AST as a tree of typed elements.

### Element Hierarchy

```
ProtobufElement (base)
+-- ProtobufFile
|   +-- ProtobufPackageStatement
|   +-- ProtobufImportStatement
|   +-- ProtobufOptionAssign
|   +-- ProtobufMessageDefinition
|   |   +-- ProtobufFieldDefinition
|   |   +-- ProtobufMapFieldDefinition
|   |   +-- ProtobufOneofDefinition
|   |   +-- ProtobufGroupDefinition
|   |   +-- (nested messages, enums)
|   +-- ProtobufServiceDefinition
|   |   +-- ProtobufRpcDefinition
|   +-- ProtobufEnumDefinition
|   |   +-- ProtobufEnumValueDefinition
|   +-- ProtobufExtendDefinition
```

### Key Patterns

- **Mixins** (`lang/psi/proto/mixin/`) — Add behavior to generated PSI classes without modifying generated code. Each element type has a mixin that provides methods like `name()`, `qualifiedName()`, `scope()`.
- **Stubs** (`lang/psi/stub/`) — Serialized summaries of PSI elements for fast indexing without full parsing. Stub types are defined in `lang/psi/stub/type/`.
- **Features** (`lang/psi/feature/`) — Interfaces that PSI elements implement to participate in cross-cutting features (symbol references, indexing, external stub data).

**Code**: `lang/psi/`
### Protobuf Editions Support

Since v2.0.0, the plugin supports [Protobuf Editions](https://protobuf.dev/editions/) — a new syntax evolution that replaces `syntax = "proto2"` / `syntax = "proto3"` with `edition = "2023"` (and future editions). The grammar accepts `EditionStatement` as an alternative to `SyntaxStatement`, and `ProtobufEditionAnnotator` provides edition-specific semantic validation.

## Indexing & Stub Layer

Enables fast symbol lookup across the entire project without parsing every file.

### Stub Indices

- `ShortNameIndex` — Find elements by simple name
- `QualifiedNameIndex` — Find elements by fully qualified name
- `ResourceTypeIndex` — Find AIP resource types

### Root Providers

Root providers tell the plugin where to find `.proto` files for import resolution:

| Provider | Source |
|----------|--------|
| `ModuleSourceRootProvider` | Module source directories |
| `LibraryRootProvider` | Project library JARs |
| `EmbeddedRootProvider` | Built-in Google proto definitions |
| `DecompiledRootProvider` | Decompiled proto descriptors |

Root providers are an **extension point** — integrations (Go, etc.) can add their own.

**Code**: `lang/root/`, `lang/psi/stub/`

## Language Support Layer

Built on top of PSI, provides the IDE features users interact with.

| Feature | Implementation | Code |
|---------|---------------|------|
| Syntax highlighting | `ProtobufHighlighter`, `ProtobufHighlightingAnnotator` | `lang/highligh/` |
| Semantic annotations | `ProtobufAnnotator`, `Protobuf2Annotator`, `Protobuf3Annotator`, `ProtobufEditionAnnotator` | `lang/annotator/` |
| Code completion | `ProtobufCompletionContributor` | `lang/completion/` |
| Code formatting | `ProtobufFormattingModelBuilder` | `lang/formatter/` |
| Import optimization | `ProtobufImportOptimizer` | `lang/formatter/` |
| Reference resolution | `ProtobufSymbolReferenceContributor` | `lang/reference/` |
| Quick fixes | `AddImportFix`, `RenameFix`, `OptimizeImportsFix` | `lang/quickfix/` |
| Find usages | `ProtobufFindUsageProvider` | `lang/usage/` |
| Code folding | `ProtobufFoldingBuilder` | `lang/folding/` |
| Refactoring | `ProtobufRefactoringSupportProvider` | `lang/reference/` |
| Documentation | `ProtobufDocumentationProvider` | `lang/docs/` |
| Structure view | `ProtobufStructureViewFactory` | `lang/ui/` |

**Code**: `lang/` subpackages

## UI & Settings Layer

User-facing configuration and visual elements.

- **Settings** (`lang/settings/`) — Plugin configuration UI (import roots, features)
- **Structure view** (`lang/ui/`) — Outline panel for proto files
- **Icons** (`ProtobufIcons.kt`) — File type, element, and gutter icons
- **Actions** (`lang/actions/`) — Editor actions (arrange field numbers)

## Integration Modules

Optional modules loaded only when their dependencies are present. Each is declared in a separate XML config file.

| Module | Config | Purpose | Documentation |
|--------|--------|---------|---------------|
| Java | `*-java.xml` | Proto <-> Java generated code navigation | [modules/java.md](modules/java.md) |
| Go | `*-go.xml` | Proto <-> Go code navigation, decompilation | [modules/go.md](modules/go.md) |
| Sisyphus | `*-sisyphus.xml` | Sisyphus Kotlin/gRPC framework support | [modules/sisyphus.md](modules/sisyphus.md) |
| gRPC Client | `*-client.xml` | gRPC request execution via HTTP Client | [modules/grpc.md](modules/grpc.md) |
| Microservices | `*-microservices.xml` | Endpoints view integration | [modules/grpc.md](modules/grpc.md) |
| AIP | (in main plugin.xml) | Google API Improvement Proposals support | [modules/aip.md](modules/aip.md) |
| Markdown | `*-markdown.xml` | Markdown rendering in proto comments | — |

## Extension Points

The plugin defines its own extension points for third-party extensions:

| Extension Point | Interface | Purpose |
|-----------------|-----------|---------|
| `rootProvider` | `ProtobufRootProvider` | Custom import root sources |
| `symbolReferenceProvider` | `ProtobufSymbolReferenceProvider` | Custom symbol reference resolution |
| `indexProvider` | `ProtobufIndexProvider` | Custom index contributions |
| `stubExternalProvider` | `ProtobufStubExternalProvider` | External stub data |
| `protocPlugin` | `ProtobufCompilerPlugin` | Custom protoc compiler plugins |

## Utility Packages

- **`string/`** — Case conversion (camelCase, snake_case, PascalCase, etc.) and English pluralization
- **`compile/`** — Protobuf compilation infrastructure (protoc integration)
- **`decompile/`** — Reconstruct `.proto` from compiled descriptors
- **`ui/`** — Shared UI components (smart tree, tooltip)