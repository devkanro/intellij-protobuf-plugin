---
tags: [design, editions, feature-set, proto2, proto3]
related: [design/annotation-layers]
refs: [docs/design/editions.md]
---
# Protobuf Editions — Feature-Set Model

Editions replace `syntax = "proto2"` / `syntax = "proto3"` with `edition = "2023"`, where each behavioral difference becomes a separately toggleable feature.

## ProtobufFeature Data Class

```kotlin
data class ProtobufFeature(
    val enumType: ProtobufEnumType,                       // OPEN | CLOSED
    val fieldPresence: ProtobufFieldPresence,             // LEGACY_REQUIRED | EXPLICIT | IMPLICIT
    val jsonFormat: ProtobufJsonFormat,                   // ALLOW | LEGACY_BEST_EFFORT
    val messageEncoding: ProtobufMessageEncoding,         // LENGTH_PREFIXED | DELIMITED
    val repeatedFieldEncoding: ProtobufRepeatedFieldEncoding,  // PACKED | EXPANDED
    val utf8Validation: ProtobufUtf8Validation,           // VERIFY | NONE
)
```

## Backward-Compatible Defaults

| Version | Enums | Presence | JSON | Repeated | UTF-8 |
|---------|-------|----------|------|----------|-------|
| PROTO2 | CLOSED | EXPLICIT | LEGACY_BEST_EFFORT | EXPANDED | NONE |
| PROTO3 | OPEN | IMPLICIT | ALLOW | PACKED | VERIFY |
| EDITION_2023 | OPEN | EXPLICIT | ALLOW | PACKED | VERIFY |

EDITION_2023 is "mostly proto3, but with explicit field presence."

## Implementation Status

| Component | Status |
|---|---|
| Grammar (`EditionStatement`) | ✅ Complete |
| PSI model (`file.edition()` API) | ✅ Complete |
| Feature-set definitions | ✅ Complete |
| `ProtobufEditionAnnotator` | ⚠️ Stub (exists but returns early, not registered) |
| Feature-driven validation | ❌ Not started (still uses syntax-based annotators) |

The model is defined but not yet wired into validation. Current annotators are production-tested; migration is a refactoring task. Per-element feature overrides are explicitly deferred.
