---
tags: [design, compiler, descriptor]
related: [design/compiler-plugin-arch, modules/grpc]
refs: [docs/design/compiler-system.md]
---
# In-Process Compiler

The plugin includes a pure-Kotlin compiler that walks the IntelliJ PSI tree directly and produces `FileDescriptorProto` objects — without external `protoc`.

## Why?

- **Zero configuration** — works immediately after installation, no PATH setup or SDK configuration
- **PSI integration** — reads directly from already-parsed PSI tree, cached via `CachedValuesManager`, invalidated when source changes
- **Graceful degradation** — wraps every element's compilation in try-catch, silently skipping malformed items. A syntax error in one message doesn't prevent the rest from compiling

## Tradeoffs Accepted

- Custom options with extension values only partially supported
- Edition-specific semantics not fully modeled
- Code generation is out of scope — produces metadata, not Java/Go/C++ code

## Primary Consumer

`ProtoFileReflection` in the gRPC module calls `Protoc.compileFiles()` to obtain descriptors, registers them with `DynamicFileSupport` from the sisyphus library, and uses them to marshal/unmarshal messages for in-IDE gRPC calls.

## Import Resolution

Stack-based with cycle detection: each compiled file's imports are pushed onto the stack, and already-compiled files (tracked by import path) are skipped.
