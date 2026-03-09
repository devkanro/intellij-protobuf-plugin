# Sisyphus Integration

**Package**: `io.kanro.idea.plugin.protobuf.sisyphus`
**Config**: `META-INF/io.kanro.idea.plugin.protobuf-sisyphus.xml`
**Dependency**: `org.jetbrains.kotlin` (optional)

## Overview

Integrates with the [Sisyphus](https://github.com/nicecraftz/sisyphus) Kotlin/gRPC framework. Sisyphus generates Kotlin DSL APIs from proto definitions — this module provides navigation between proto sources and the generated Kotlin code.

## Features

### Line Markers

- **Proto -> Kotlin**: Gutter icons on definitions linking to Sisyphus-generated Kotlin classes
- **Kotlin -> Proto**: Gutter icons on generated Kotlin types linking back to proto

### Find Usages

Kotlin code using Sisyphus-generated types will appear in "Find Usages" for the proto definition.

### Index

`SisyphusIndexProvider` indexes proto elements with their Sisyphus Kotlin class names, following Sisyphus naming conventions.

## Key Files

| File | Purpose |
|------|---------|
| `Extensions.kt` | Extension declarations |
| `SisyphusIndexProvider.kt` | Stub index contributions |
| `SisyphusNameIndex.kt` | Name index |
| `SisyphusKotlinLineMarkerProvider.kt` | Kotlin-side gutter icons |
| `SisyphusProtobufLineMarkerProvider.kt` | Proto-side gutter icons |
| `SisyphusFindUsageFactory.kt` | Find usage integration |
| `Names.kt` | Sisyphus naming conventions |