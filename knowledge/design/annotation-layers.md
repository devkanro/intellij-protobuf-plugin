---
tags: [design, annotation, validation, proto2, proto3]
related: [design/annotation-quick-fixes, design/editions-feature-model]
refs: [docs/design/annotation-system.md]
---
# Annotation System — Layered Validation

Protobuf is three divergent dialects sharing one syntax. Proto2 *requires* field labels; proto3 *forbids* `required`. Proto3 bans groups, extensions, default values — all legal in proto2. A single validator would devolve into a tangle of conditionals.

## Per-Version Annotators

| Layer | Class | Activates When |
|-------|-------|----------------|
| Universal | `ProtobufAnnotator` | Always (all `.proto` files) |
| Proto2 | `Protobuf2Annotator` | `syntax` is `"proto2"` or unspecified |
| Proto3 | `Protobuf3Annotator` | `syntax` is exactly `"proto3"` |
| Editions | `ProtobufEditionAnnotator` | `edition()` is non-null |

Each version annotator self-gates with an early return. Gating is cheap — a single `file.syntax()` call.

## Annotators Over Inspections

Protobuf errors are specification violations — `protoc` will reject the file. Making them suppressible via inspections would let users silence real errors. Annotators mirror the compiler: always on, not negotiable. Naming convention warnings use `WEAK_WARNING` severity.

## Trackers (Cached Utilities)

`FileTracker`, `NumberTracker`, and `ScopeTracker` use `CachedValuesManager` to compute constraint sets once per PSI modification. Individual `visit()` calls check a single element against the cached set — O(1) lookup, avoiding O(n²) recomputation.

**Record-Visit pattern**: Each tracker records all definitions in a scope during construction, then exposes `visit(element, holder)` methods that check one element against the recorded set.
