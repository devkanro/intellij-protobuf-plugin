---
tags: [design, psi, feature-interfaces]
related: [design/psi-mixin-injection, design/dual-psi-hierarchies]
refs: [docs/design/psi-and-mixin.md]
---
# Feature Interfaces

The `lang/psi/feature/` package defines cross-cutting interfaces that decouple IDE features from concrete PSI types:

| Interface | Concern |
|-----------|---------|
| `NamedElement` | Anything with a user-visible name |
| `ReferenceElement` | Anything that refers to another element |
| `QualifiedElement<T>` | Dotted name chains (`a.b.c`) with root/leaf traversal |
| `BodyOwner` / `BodyElement` | Elements with `{}`-delimited bodies |
| `DocumentOwner` / `DocumentElement` | Doc-comment association |
| `LookupableElement` | Completion item generation |
| `FoldingElement` | Code folding regions |
| `ValueElement<T>` / `ValueAssign` | Option value typing |

## Why Separate Interfaces?

The combinations are irregular. A `MessageDefinition` is a `BodyOwner`, `NamedElement`, and `DocumentOwner`. A `FieldDefinition` is a `NamedElement` and `OptionOwner` but not a `BodyOwner`. An `ImportStatement` is a `ReferenceElement` pointing at a file. Interfaces let the grammar declare exactly which capabilities each rule has.

Without them, every IDE feature would need `instanceof` chains against concrete PSI classes. With them, implementing `NamedElement` on a new type automatically enables rename refactoring.
