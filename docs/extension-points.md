# Extension Points

The plugin defines custom extension points that allow third-party plugins to extend its functionality. All extension points are dynamic (support runtime loading/unloading).

## rootProvider

**Interface**: `io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootProvider`

Provides import root directories for proto file resolution. When the plugin encounters an `import` statement, it queries all registered root providers to find the imported file.

### Built-in Providers

| Provider | Source |
|----------|--------|
| `ModuleSourceRootProvider` | Module source directories marked as proto roots |
| `LibraryRootProvider` | JAR libraries containing `.proto` files |
| `EmbeddedRootProvider` | Google's standard proto definitions bundled with the plugin |
| `DecompiledRootProvider` | Proto files reconstructed from compiled descriptors |
| `GoRootProvider` | Go module proto paths (from `golang` integration) |

### Usage

```xml
<extensions defaultExtensionNs="io.kanro.idea.plugin.protobuf">
    <rootProvider implementation="com.example.MyRootProvider"/>
</extensions>
```

```kotlin
class MyRootProvider : ProtobufRootProvider {
    override fun roots(context: ProtobufRootProviderContext): List<VirtualFile> {
        // Return directories containing .proto files
    }
}
```

## symbolReferenceProvider

**Interface**: `io.kanro.idea.plugin.protobuf.lang.psi.feature.ProtobufSymbolReferenceProvider`

Provides custom symbol references beyond standard proto references. Used by AIP to resolve resource type names in string literals.

### Built-in Providers

- `AipResourceReferenceProvider` — Resolves `(google.api.resource_reference)` type strings

## indexProvider

**Interface**: `io.kanro.idea.plugin.protobuf.lang.psi.feature.ProtobufIndexProvider`

Contributes index entries for proto elements, enabling navigation between proto definitions and generated code.

### Built-in Providers

| Provider | Purpose |
|----------|---------|
| `JavaIndexProvider` | Index Java generated class names |
| `GoIndexProvider` | Index Go generated type names |
| `SisyphusIndexProvider` | Index Sisyphus Kotlin type names |

### Usage

```kotlin
class MyIndexProvider : ProtobufIndexProvider {
    override fun buildIndex(element: ProtobufElement): Map<StubIndexKey<*, *>, List<String>> {
        // Return index entries for this element
    }
}
```

## stubExternalProvider

**Interface**: `io.kanro.idea.plugin.protobuf.lang.psi.feature.ProtobufStubExternalProvider`

Provides additional external data to be stored alongside PSI stubs. Used by Java integration to cache Java option values.

### Built-in Providers

- `FileJavaOptionsProvider` — Extracts `java_package`, `java_outer_classname`, `java_multiple_files` options

## protocPlugin

**Interface**: `io.kanro.idea.plugin.protobuf.compile.ProtobufCompilerPlugin`

Hooks into the protobuf compilation process, allowing custom code generation plugins.

### Built-in Compilers

| Compiler | Purpose |
|----------|---------|
| `FileCompiler` | File-level compilation |
| `MessageCompiler` | Message type compilation |
| `EnumCompiler` | Enum type compilation |
| `ServiceCompiler` | Service compilation |