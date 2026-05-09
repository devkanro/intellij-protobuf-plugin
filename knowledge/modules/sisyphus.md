---
tags: [module, sisyphus, kotlin, framework]
related: [design/external-data-system, modules/java]
refs: [docs/modules/sisyphus.md]
---
# Sisyphus Integration Module

**Package**: `io.kanro.idea.plugin.protobuf.sisyphus`
**Config**: `META-INF/io.kanro.idea.plugin.protobuf-sisyphus.xml`
**Dependency**: `org.jetbrains.kotlin` (optional)

Integrates with the [Sisyphus](https://github.com/nicecraftz/sisyphus) Kotlin/gRPC framework, which generates Kotlin DSL APIs from proto definitions.

## Features

- **Line Markers**: Bidirectional gutter icons between proto definitions and Sisyphus-generated Kotlin classes
- **Find Usages**: Kotlin code using Sisyphus-generated types appears in "Find Usages" for proto definitions
- **Index**: `SisyphusIndexProvider` indexes proto elements with their Sisyphus Kotlin class names

## Key Files

| File | Purpose |
|------|---------|
| `SisyphusIndexProvider.kt` | Stub index contributions |
| `SisyphusNameIndex.kt` | Name index |
| `SisyphusKotlinLineMarkerProvider.kt` | Kotlin-side gutter icons |
| `SisyphusProtobufLineMarkerProvider.kt` | Proto-side gutter icons |
| `SisyphusFindUsageFactory.kt` | Find usage integration |
| `Names.kt` | Sisyphus naming conventions |
