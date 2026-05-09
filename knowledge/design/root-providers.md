---
tags: [design, symbol-resolution, root-providers, imports]
related: [design/symbol-resolution-algorithm, architecture/extension-points]
refs: [docs/design/symbol-resolution.md]
---
# Root Providers

The universe of resolvable proto files is project-dependent and must be pluggable.

## How It Works

- `ProtobufRootProvider` returns a list of `ProtobufRoot` (name + VirtualFile directory) for a given PSI context
- `ProtobufRootResolver` collects roots from all providers, then deduplicates: providers with same `id()` collapse (first wins), roots with same `name` collapse
- Import statements like `import "google/protobuf/timestamp.proto"` are resolved by searching the relative path against every collected root

## Built-in Providers

| Provider | Source |
|----------|--------|
| `ModuleSourceRootProvider` | Module source directories marked as proto roots |
| `LibraryRootProvider` | JAR libraries containing `.proto` files |
| `EmbeddedRootProvider` | Google's standard proto definitions bundled with the plugin |
| `DecompiledRootProvider` | Proto files reconstructed from compiled descriptors |
| `GoRootProvider` | Go module proto paths |

## Caching

Root computation is expensive (scanning module dependencies, JAR contents). Providers extend `CachedProtobufRootProvider` which caches results per-file using `CachedValuesManager` with a build-system-aware modification tracker.
