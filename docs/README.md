# Documentation

Welcome to the IntelliJ Protobuf Plugin documentation. Start here to find what you need.

## For Newcomers

| Doc | What You'll Learn |
|-----|-------------------|
| [Overview](overview.md) | What the plugin does and why it exists |
| [Architecture](architecture.md) | How the pieces fit together (high level) |

## For Contributors

| Doc | What You'll Learn |
|-----|-------------------|
| [Getting Started](getting-started.md) | Build, run, test the plugin |
| [Contributing](contributing.md) | Code style, adding features and modules |

## Design Documents

Deep-dive into *why* each subsystem is designed the way it is. Read these when you need to understand or modify a specific area.

| Doc | Core Question |
|-----|---------------|
| [PSI & Mixin Pattern](design/psi-and-mixin.md) | Why use mixins instead of inheritance for PSI behavior? |
| [Symbol Resolution](design/symbol-resolution.md) | Why two-phase resolution with a scope hierarchy? |
| [Stub Indexing](design/stub-indexing.md) | Why stubs, and what tradeoffs in the index design? |
| [Annotation System](design/annotation-system.md) | Why per-version annotators instead of one validator? |
| [Code Completion](design/code-completion.md) | Why a provider-per-context model? |
| [Compiler System](design/compiler-system.md) | Why build an in-process compiler instead of calling protoc? |
| [ProtoText Support](design/prototext.md) | Why a separate language implementation? |
| [Protobuf Editions](design/editions.md) | Why a feature-set model over syntax version flags? |

## Extension & Integration

| Doc | What You'll Learn |
|-----|-------------------|
| [Extension Points](extension-points.md) | Public API for extending the plugin |
| [Java Module](modules/java.md) | Java/Kotlin code generation integration |
| [Go Module](modules/go.md) | Go support and descriptor decompilation |
| [gRPC Module](modules/grpc.md) | gRPC endpoints and request execution |
| [AIP Module](modules/aip.md) | Google API design spec support |
| [Sisyphus Module](modules/sisyphus.md) | Sisyphus framework integration |
