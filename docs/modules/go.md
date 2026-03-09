# Go Integration

**Package**: `io.kanro.idea.plugin.protobuf.golang`
**Config**: `META-INF/io.kanro.idea.plugin.protobuf-go.xml`
**Dependency**: `org.jetbrains.plugins.go` (optional)

## Overview

Provides navigation between `.proto` definitions and generated Go code, plus decompilation of Go protobuf descriptors back to `.proto` source.

## Features

### Line Markers

- **Proto -> Go**: Gutter icons on definitions linking to generated `.pb.go` files
- **Go -> Proto**: Gutter icons on generated Go types linking back to proto source

### Decompilation

When a Go module contains compiled proto descriptors (embedded in `.pb.go` files), the plugin can reconstruct the original `.proto` definitions. This is useful for navigating into dependencies that only ship compiled descriptors.

- `GoDecompileService` — Orchestrates the decompilation process
- `GoDecompileLineMarker` — Gutter icon to trigger decompilation

### Import Root

`GoRootProvider` adds Go module proto paths to the import resolution chain, so imports in proto files can resolve to protos from Go dependencies.

### Index

`GoIndexProvider` indexes proto elements with their Go type names using protobuf-Go naming conventions.

## Key Files

| File | Purpose |
|------|---------|
| `Extension.kt` | Extension point declarations |
| `GoRootProvider.kt` | Import root from Go modules |
| `GoIndexProvider.kt` | Stub index contributions |
| `GoLineMarkerProvider.kt` | Go-side gutter icons |
| `GoDecompileService.kt` | Proto decompilation from Go |
| `GoDecompileLineMarker.kt` | Decompile gutter action |
| `GoNameIndex.kt` | Name index |
| `GoUnimplementedServerNameIndex.kt` | gRPC unimplemented server index |
| `Names.kt` | Go naming conventions |