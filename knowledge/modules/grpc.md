---
tags: [module, grpc, http-client, microservices]
related: [design/compiler-in-process, modules/java]
refs: [docs/modules/grpc.md]
---
# gRPC Support Module

**Package**: `io.kanro.idea.plugin.protobuf.grpc`
**Config**: `META-INF/io.kanro.idea.plugin.protobuf-client.xml`, `META-INF/io.kanro.idea.plugin.protobuf-microservices.xml`
**Dependencies**: `com.jetbrains.restClient` (optional), `com.intellij.modules.microservices` (optional)

## Features

- **Request Execution**: Execute gRPC requests via IntelliJ's HTTP Client — native gRPC (HTTP/2), gRPC-Web, and HTTP Transcoding (RESTful JSON)
- **Gutter Icons**: One-click request execution from `rpc` definitions, generates pre-configured HTTP Client request files
- **Endpoint Discovery**: Exposes gRPC services in IntelliJ's Endpoints/Microservices view
- **Request Editor**: JSON field completion for gRPC request bodies using proto message schema

## Subpackages

| Package | Purpose |
|---------|---------|
| `grpc/request/` | Request execution, MIME types, content handling |
| `grpc/gutter/` | Gutter icon providers |
| `grpc/index/` | Service method indices (`ServiceMethodIndex`) |
| `grpc/editor/` | JSON field completion in request bodies |
| `microservices/` | Endpoints view integration |
