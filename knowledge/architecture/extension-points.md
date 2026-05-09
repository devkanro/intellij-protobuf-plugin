---
tags: [architecture, extension-points, api]
related: [architecture/layer-diagram, modules/java, modules/go]
refs: [docs/extension-points.md]
---
# Extension Points

Five extension points allow third-party plugins to extend the core. All are dynamic (support runtime loading/unloading).

## rootProvider

**Interface**: `ProtobufRootProvider`

Provides import root directories for proto file resolution. Built-in: `ModuleSourceRootProvider`, `LibraryRootProvider`, `EmbeddedRootProvider`, `DecompiledRootProvider`, `GoRootProvider`.

```xml
<extensions defaultExtensionNs="io.kanro.idea.plugin.protobuf">
    <rootProvider implementation="com.example.MyRootProvider"/>
</extensions>
```

## symbolReferenceProvider

**Interface**: `ProtobufSymbolReferenceProvider`

Custom symbol references beyond standard proto references. Used by AIP to resolve resource type names in string literals.

## indexProvider

**Interface**: `ProtobufIndexProvider`

Contributes index entries for proto elements, enabling navigation between proto definitions and generated code. Built-in: `JavaIndexProvider`, `GoIndexProvider`, `SisyphusIndexProvider`.

## stubExternalProvider

**Interface**: `ProtobufStubExternalProvider`

Provides additional external data stored alongside PSI stubs. Used by Java integration to cache Java option values (`java_package`, `java_outer_classname`, `java_multiple_files`).

## protocPlugin

**Interface**: `ProtobufCompilerPlugin`

Hooks into the protobuf compilation process. 10 built-in compilers cover File, Message, Field, Oneof, MapEntry, MapField, Enum, EnumValue, Service, and ServiceMethod compilation.
