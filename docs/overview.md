# Plugin Overview

## What Is This?

IntelliJ Protobuf Language Plugin is a full-featured [Protocol Buffers](https://protobuf.dev/) language plugin for JetBrains IDEs. It replaces the official JetBrains Protobuf plugin with a more complete implementation: deep semantic understanding, cross-file symbol resolution, multi-language integration, and support for modern protobuf features like Editions.

## What Can It Do?

The plugin turns your IDE into a protobuf-aware development environment:

- **Understands your schema** — not just syntax highlighting, but full semantic analysis. It knows that a field type references a message defined in another file, that an import is unused, or that a field number conflicts.
- **Navigates like code** — go-to-definition across files and imports, find all usages of a message or field, rename with cross-file updates.
- **Catches mistakes early** — real-time error highlighting for naming conventions, type mismatches, proto2/proto3 rule violations, and AIP spec compliance.
- **Works with your stack** — language-specific integration for Java, Go, Kotlin (Sisyphus), and gRPC. The plugin understands how proto definitions map to generated code.
- **Speaks text format** — full editor support for `.textproto` files with schema-based completion and validation.

## Key Concepts

### Proto Files as First-Class Code

The plugin treats `.proto` files the way IntelliJ treats Java or Kotlin — building a full PSI (Program Structure Interface) tree, maintaining stub indices for fast lookup, and resolving references across the entire project scope including libraries and SDKs.

### Multi-Source Symbol Resolution

Proto files can live in many places: your project sources, library JARs, the protobuf SDK, or decompiled descriptors. The plugin's root provider system aggregates all these sources transparently, so imports "just work" regardless of where the proto file lives.

### Integration Modules

Language-specific features (Java class navigation, Go decompilation, gRPC endpoint discovery) are implemented as optional modules that activate only when the relevant IDE plugins are present. This keeps the core lightweight while enabling deep integration.

### Protobuf Editions

The plugin supports the newer [Protobuf Editions](https://protobuf.dev/editions/overview/) system (`edition = "2023"`), which replaces the `syntax = "proto2"` / `syntax = "proto3"` model with configurable feature sets.

## Where to Go Next

- **Want to contribute?** → [Getting Started](getting-started.md)
- **Understand the architecture?** → [Architecture](architecture.md)
- **Deep-dive into a subsystem?** → [Design Documents](design/)
- **Extend the plugin?** → [Extension Points](extension-points.md)
- **Module-specific docs?** → [Modules](modules/)
