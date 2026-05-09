---
tags: [design, psi, mixin, grammar-kit]
related: [design/feature-interfaces, design/stub-system, design/dual-psi-hierarchies]
refs: [docs/design/psi-and-mixin.md, src/main/grammar/protobuf.bnf]
---
# PSI & Mixin Injection

Grammar-Kit BNF files (`protobuf.bnf`, `prototext.bnf`) are the single source of truth for PSI structure. Every grammar rule can declare three orthogonal extension points:

```bnf
MessageDefinition ::= message Identifier MessageBody {
    implements=[ "...ProtobufElement", "...BodyOwner", "...ProtobufOptionOwner" ]
    mixin="...ProtobufMessageDefinitionMixin"
    stubClass="...ProtobufMessageStub"
}
```

| Attribute | Role |
|-----------|------|
| `implements` | Bolt on cross-cutting feature interfaces via composition |
| `mixin` | Inject an abstract base class that provides default behavior |
| `stubClass` | Wire up stub serialization for index-time access |

## Why Not Hand-Write PSI?

The grammar *will* change (new editions, custom options, AIP annotations). Changing a grammar rule auto-regenerates the PSI class; behavior stays in mixins and feature interfaces that survive regeneration.

## Three-Tier Behavior Model

1. **Feature interfaces** — contracts (`NamedElement`, `BodyOwner`)
2. **Mixins** — default implementations injected at generation time
3. **Extension functions** — domain helpers (e.g., `ProtobufTypeName.absolutely()`, `ProtobufImportStatement.public()`)

Extensions are preferred when the behavior is a pure query that doesn't need to override a generated method or participate in the stub lifecycle. PSI element behavior goes in mixins, not generated classes.
