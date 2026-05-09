---
tags: [design, psi, prototext]
related: [design/psi-mixin-injection, design/prototext-schema-linking]
refs: [docs/design/psi-and-mixin.md, docs/design/prototext.md]
---
# Dual PSI Hierarchies

Proto (`.proto`) and ProtoText (`.textproto`) have separate grammars, parsers, and PSI element hierarchies (`Protobuf*` vs `ProtoText*`). Yet both implement the *same* feature interfaces from `lang/psi/feature/`.

## Why Not One Grammar?

The syntaxes differ enough:
- Different comments: proto uses `//` and `/* */`, textproto uses `#`
- Different field syntax: proto is `int32 bar = 1;`, textproto is `bar: 42`
- Textproto has no type definitions

Merging would make the BNF harder to read and error-recovery worse.

## What IS Shared

The reference resolution infrastructure. TextProto references resolve *to* proto PSI classes (`ProtobufFieldDefinition`, `ProtobufEnumValueDefinition`, `ProtobufMessageDefinition`). Both use `ProtobufSymbolResolver` for scope-based lookup and the same stub indexes for cross-file resolution.

The separation is at the language boundary; the integration is at the semantic boundary.
