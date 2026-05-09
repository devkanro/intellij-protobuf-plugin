---
tags: [design, compiler, plugin-architecture, state-machine]
related: [design/compiler-in-process, architecture/extension-points]
refs: [docs/design/compiler-system.md]
---
# Compiler Plugin Architecture

Compilation is split across 10 `ProtobufCompilerPlugin` implementations, each responsible for one element type, registered as IntelliJ extension points.

## Built-in Plugins

| Plugin | Compiles | Key Logic |
|---|---|---|
| `FileCompiler` | Files → `FileDescriptorProto` | Package, syntax, dependency extraction |
| `MessageCompiler` | Messages → `DescriptorProto` | Recursive nesting |
| `MessageFieldCompiler` | Fields → `FieldDescriptorProto` | Type resolution, label inference |
| `MessageOneofCompiler` | Oneof groups | Field grouping |
| `MessageMapEntryCompiler` | Map fields → synthetic `Entry` types | Hidden key/value messages |
| `MessageMapFieldCompiler` | Map field wrappers | Links to synthetic entries |
| `EnumCompiler` | Enums → `EnumDescriptorProto` | Name and value extraction |
| `EnumValueCompiler` | Enum values | Individual constants |
| `ServiceCompiler` | Services → `ServiceDescriptorProto` | RPC method routing |
| `ServiceMethodCompiler` | RPC methods → `MethodDescriptorProto` | Input/output type resolution |

## State Machine Model

Each PSI node becomes a typed state (`ProtobufCompilingState<TDesc, TPsi>`). `CompileContext` dispatches states to plugins. States carry `target()` (mutable descriptor), `element()` (source PSI), and `parent()`.

States decouple *traversal* from *transformation*: `CompileContext` handles traversal, plugins handle transformation. Typed generics prevent `MessageCompiler` from receiving an `EnumCompilingState` at compile time.
