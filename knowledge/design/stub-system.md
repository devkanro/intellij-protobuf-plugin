---
tags: [design, stubs, indexing, performance]
related: [design/external-data-system, design/psi-mixin-injection, architecture/extension-points]
refs: [docs/design/stub-indexing.md]
---
# Stub Indexing System

Stubs are lightweight serialized snapshots of PSI elements that enable fast symbol lookup without parsing every file.

## What Gets Stubbed

**Stubbed** (11 types): Message, Enum, EnumValue, Service, Rpc, Field, MapField, Oneof, Group, Extend, PackageName.

**Not stubbed**: field types, field numbers, option values, comments, default values, method request/response types.

The boundary: stubs capture *identity* (names) and *structure* (nesting). They skip *content* (types, options) because content requires cross-file resolution.

## Three Core Indices

| Index | Key | Optimizes |
|-------|-----|-----------|
| **ShortNameIndex** | Simple name (`"Timestamp"`) | Completion — fuzzy match |
| **QualifiedNameIndex** | Full path (`"google.protobuf.Timestamp"`) | Reference resolution — exact lookup |
| **ResourceTypeIndex** | AIP resource type | AIP compliance |

All three populated in a single `indexStub()` pass.

## Scope-Carrying Stubs

Qualified names are computed from the stub tree, not stored verbatim. `qualifiedName()` walks the parent stub chain: file stub provides package prefix, message stubs append names. This avoids duplicating package paths hundreds of times per file.

## Dual Constructors

Every stub-based mixin has two constructors — AST mode (parsing) and stub mode (index loading). The same `ProtobufMessageDefinition` interface works whether backed by a parsed AST or deserialized stub.

## Single Global Stub Version

`getStubVersion() = 1`. Changing stub format for any element type invalidates the entire cache. The `Map<String, String>` external data absorbs most variability schema-free.
