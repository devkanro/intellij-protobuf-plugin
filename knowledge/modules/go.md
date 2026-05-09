---
tags: [module, go, decompilation, navigation]
related: [design/external-data-system, architecture/extension-points]
refs: [docs/modules/go.md]
---
# Go Integration Module

**Package**: `io.kanro.idea.plugin.protobuf.golang`
**Config**: `META-INF/io.kanro.idea.plugin.protobuf-go.xml`
**Dependency**: `org.jetbrains.plugins.go` (optional)

## Features

- **Line Markers**: Bidirectional gutter icons between `.proto` definitions and generated `.pb.go` files
- **Decompilation**: Reconstructs original `.proto` definitions from compiled proto descriptors embedded in `.pb.go` files. Useful for navigating dependencies that only ship compiled descriptors
- **Import Root**: `GoRootProvider` adds Go module proto paths to the import resolution chain
- **Index**: `GoIndexProvider` indexes proto elements with their Go type names using protobuf-Go naming conventions

## Key Files

| File | Purpose |
|------|---------|
| `GoRootProvider.kt` | Import root from Go modules |
| `GoIndexProvider.kt` | Stub index contributions |
| `GoDecompileService.kt` | Proto decompilation from Go |
| `GoLineMarkerProvider.kt` | Go-side gutter icons |
| `Names.kt` | Go naming conventions |
