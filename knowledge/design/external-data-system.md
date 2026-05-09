---
tags: [design, stubs, indexing, java, go, extensibility]
related: [design/stub-system, modules/java, modules/go, modules/sisyphus]
refs: [docs/design/stub-indexing.md]
---
# External Data System

Each stub carries a `Map<String, String>` of external data, populated by `ProtobufStubExternalProvider` implementations at stub-creation time.

## Problem

Protobuf files contain language-specific options (`java_package`, `go_package`, `json_name`) that control code generation. The plugin needs this data for cross-language navigation, but it shouldn't be baked into the core stub format because:

1. Not all users need all languages
2. New languages appear (stub version bump for each would invalidate caches)
3. Custom options are project-specific

## Two-Phase Design

- **`stubExternalProvider`**: Extracts option values during stub creation. Example: `FileJavaOptionsProvider` reads `java_package`, `java_outer_classname`, `java_multiple_files`.

- **`indexProvider`**: Builds custom index entries from stub data during indexing. Example: `JavaIndexProvider` computes gRPC stub class names (`*ImplBase`, `*BlockingStub`, `*FutureStub`, `*CoroutineStub`).

## Built-in Providers

| Provider | Index | Purpose |
|----------|-------|---------|
| `JavaIndexProvider` | `JavaNameIndex` | Java generated class names |
| `GoIndexProvider` | `GoNameIndex` | Go generated type names |
| `SisyphusIndexProvider` | `SisyphusNameIndex` | Sisyphus Kotlin type names |
| `ServiceMethodIndexProvider` | `ServiceMethodIndex` | gRPC method paths |

The core stub format (`Array<String>` + `Map<String, String>`) stays stable while language-specific logic lives in optional modules.
