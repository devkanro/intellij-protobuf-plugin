# gRPC Support

**Package**: `io.kanro.idea.plugin.protobuf.grpc`
**Config**: `META-INF/io.kanro.idea.plugin.protobuf-client.xml`, `META-INF/io.kanro.idea.plugin.protobuf-microservices.xml`
**Dependencies**: `com.jetbrains.restClient` (optional), `com.intellij.modules.microservices` (optional)

## Overview

Provides gRPC request execution via IntelliJ's HTTP Client, gutter icons for running requests, and endpoint discovery for the Microservices view.

## Features

### gRPC Request Execution

Execute gRPC requests directly from the IDE using IntelliJ's HTTP Client infrastructure:

- **Native gRPC** — Binary proto requests over HTTP/2
- **gRPC-Web** — Browser-compatible gRPC
- **HTTP Transcoding** — RESTful JSON requests to gRPC services

### Gutter Icons

Gutter icons on `rpc` definitions allow one-click request execution. Clicking generates an HTTP Client request file pre-configured for the service.

### Endpoint Discovery

The Microservices module exposes gRPC services in IntelliJ's Endpoints view, making them discoverable alongside REST APIs.

### Request Editor

JSON field completion for gRPC request bodies, understanding the proto message schema.

## Subpackages

| Package | Purpose |
|---------|---------|
| `grpc/request/` | Request execution support, MIME types, content handling |
| `grpc/gutter/` | Gutter icon providers for run actions |
| `grpc/index/` | Service method indices for lookup |
| `grpc/referece/` | Reference contributors for HTTP request files |
| `grpc/editor/` | JSON field completion in request bodies |
| `microservices/` | Endpoints view integration |

## Key Files

| File | Purpose |
|------|---------|
| `Utils.kt` | gRPC utility functions |
| `request/GrpcRequestExecutionSupport.kt` | Core request execution |
| `request/GrpcMimeTypeProvider.kt` | MIME type registration |
| `gutter/GrpcRunRequestGutterProvider.kt` | Run gutter icons |
| `index/ServiceMethodIndex.kt` | Service method stub index |
| `microservices/GrpcEndpointsProvider.kt` | Endpoints integration |