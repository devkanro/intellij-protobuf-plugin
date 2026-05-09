---
tags: [design, symbol-resolution, scope]
related: [design/symbol-resolution-algorithm, design/root-providers]
refs: [docs/design/symbol-resolution.md]
---
# Scope Hierarchy

Scopes are modeled with two composable interfaces:
- **`ProtobufScope`** — container with a qualified name that holds children
- **`ProtobufScopeItem`** — anything inside a scope

A `ProtobufMessageDefinition` implements *both*: it's a scope item in its parent and a scope containing its own fields/nested types. `qualifiedName()` is always `parent.scope() + name`, computed recursively.

## Virtual Scopes

Constructs like `oneof` and `extend` look like scopes in the grammar but *don't* create new naming scopes in protobuf — a oneof's fields belong to the enclosing message. `ProtobufVirtualScope` marks these as transparent: the `items()` helper unpacks their children directly into the parent scope's item list.

This prevents oneof and extend from hiding fields during resolution while preserving the syntactic structure for formatting.

## Resolution Direction

- **Down**: iterate a scope's items and recurse into nested scopes when the first component matches
- **Up** (`resolveInCurrent`): hop from scope to parent scope looking for matches
