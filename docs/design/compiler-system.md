# Compiler System

## The Problem

An IDE plugin for protobuf needs more than parsing. Features like gRPC request testing,
cross-file type resolution, and option validation all require **descriptor metadata** — the
same `FileDescriptorProto` structures that `protoc` produces. Without descriptors, the
plugin can't know that a field's type resolves to a specific message across imports, or
marshal a message to JSON for a gRPC test call.

The question is: where do those descriptors come from? The obvious answer — call `protoc` —
has serious usability problems. Users would need `protoc` installed, configured, and
matching the proto version in their project. The plugin would break silently when paths are
wrong or versions mismatch. Build tool integration varies wildly across ecosystems.

As the comment in `Protoc.kt` notes: *"Maybe not include all feature of official protoc
compiler."* The plugin intentionally trades completeness for portability.

## Design Decisions

### Decision 1: In-Process Compilation

**Choice:** Build a pure-Kotlin compiler that walks the IntelliJ PSI tree directly and
produces `FileDescriptorProto` objects. No external process, no subprocess, no `protoc`
binary.

**Why?**

- **Zero configuration.** The plugin works immediately after installation — no PATH setup,
  no SDK configuration, no build tool integration required.
- **PSI integration.** The compiler reads directly from the already-parsed PSI tree,
  avoiding double-parsing. Results are cached via `CachedValuesManager` and invalidated
  automatically when the source changes (keyed to `PsiModificationTracker`).
- **Graceful degradation.** The compiler wraps every element's compilation in a
  try-catch, silently skipping malformed items. A syntax error in one message doesn't
  prevent the rest of the file from compiling. This is critical for an IDE where files
  are *always* in a partially-edited state.

**Tradeoffs accepted:**

- Custom options with extension values are only partially supported
- Edition-specific semantics are not fully modeled
- Code generation is out of scope — this compiler produces metadata, not Java/Go/C++ code

**Primary consumer:** `ProtoFileReflection` in the gRPC module calls
`Protoc.compileFiles()` to obtain descriptors, registers them with `DynamicFileSupport`
from the sisyphus library, and uses them to marshal/unmarshal messages for in-IDE gRPC
calls.

### Decision 2: Plugin-Based Architecture

**Choice:** Compilation is split across 10 `ProtobufCompilerPlugin` implementations, each
responsible for one element type. They are registered as IntelliJ extension points and
loaded dynamically.

The built-in plugins are:

| Plugin | Compiles | Key Logic |
|---|---|---|
| `FileCompiler` | Files → `FileDescriptorProto` | Package, syntax, dependency extraction |
| `MessageCompiler` | Messages → `DescriptorProto` | Recursive nesting, nested type routing |
| `MessageFieldCompiler` | Fields → `FieldDescriptorProto` | Type resolution (built-in vs custom), label inference, proto3 optional |
| `MessageOneofCompiler` | Oneof groups | Field grouping within oneof containers |
| `MessageMapEntryCompiler` | Map fields → synthetic `Entry` types | Generates hidden key/value message types |
| `MessageMapFieldCompiler` | Map field wrappers | Links map fields to their synthetic entries |
| `EnumCompiler` | Enums → `EnumDescriptorProto` | Name and value extraction |
| `EnumValueCompiler` | Enum values | Individual constant definitions |
| `ServiceCompiler` | Services → `ServiceDescriptorProto` | RPC method routing |
| `ServiceMethodCompiler` | RPC methods → `MethodDescriptorProto` | Input/output type resolution, streaming flags |

**Why not a monolithic compiler?**

- **Isolation.** A failure in `EnumValueCompiler` doesn't crash `MessageCompiler`. Each
  plugin catches its own exceptions.
- **Extensibility.** The `protocPlugin` extension point means third-party IntelliJ plugins
  can inject custom compilation steps (e.g., for custom options or proto extensions)
  without modifying the core.
- **Clarity.** Each plugin's scope is obvious from its name and type parameter.
  `MessageFieldCompiler` only ever sees `MessageFieldCompilingState`.

### Decision 3: State Machine Model

**Choice:** Each PSI node being compiled becomes a typed state object in a hierarchy. The
`CompileContext` dispatches states to plugins, and plugins create child states for nested
elements.

The state hierarchy mirrors the proto structure:

```
FileCompilingState
├── MessageCompilingState
│   ├── MessageFieldCompilingState
│   ├── MessageOneofCompilingState
│   │   └── MessageFieldCompilingState
│   └── MessageMapEntryCompilingState
│       └── MessageMapFieldCompilingState
├── EnumCompilingState
│   └── EnumValueCompilingState
└── ServiceCompilingState
    └── ServiceMethodCompilingState
```

Each state carries two things:
- `target()` — the mutable protobuf descriptor being populated
- `element()` — the source PSI element being compiled

Sub-states also carry `parent()`, allowing a field compiler to access its containing
message's descriptor when needed.

**Why states instead of direct recursion?** States decouple *traversal* from
*transformation*. The `CompileContext` handles traversal — deciding which plugins see which
states. Plugins handle transformation — populating descriptors. This means a new plugin can
participate in compilation without understanding or modifying the traversal logic.

**Why typed states?** The generic signature
`ProtobufCompilingState<TDesc: MutableMessage, TPsi: ProtobufElement>` means
`MessageCompiler` physically cannot receive an `EnumCompilingState`. Type errors are caught
at compile time, not runtime.

## How It Fits Together

```
.proto file → PSI Tree (parser)
                  ↓
              Protoc.compileFiles()
                  ↓
              Stack-based file traversal (handles imports, detects cycles)
                  ↓
              FileCompilingState created per file
                  ↓
              CompileContext.advance(state) dispatches to all registered plugins
                  ↓
              Plugins create child states → dispatched recursively
                  ↓
              FileDescriptorSet (collection of populated descriptors)
                  ↓
              ProtoFileReflection registers descriptors for gRPC testing
```

Import resolution is stack-based with cycle detection: each compiled file's imports are
pushed onto the stack, and already-compiled files (tracked by import path) are skipped.

## Key Insight

This compiler exists at a specific point on the completeness-usability spectrum. A full
`protoc` reimplementation would be a massive undertaking with diminishing returns — the IDE
features that need descriptors (gRPC testing, type resolution, option validation) need
*structure*, not *code generation semantics*. By building just enough compiler to produce
accurate descriptors, and making it resilient to partial/broken input, the plugin delivers
a "just works" experience that would be impossible with an external tool dependency.
