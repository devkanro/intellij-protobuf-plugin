# Protobuf Editions

## The Problem

Protobuf's original versioning model — `syntax = "proto2"` vs `syntax = "proto3"` — created
a binary world where behaviors were bundled into monolithic versions. Proto3 changed
*multiple things at once*: open enums, implicit field presence, packed repeated encoding,
required UTF-8 validation. You couldn't adopt proto3's open enums without also losing
explicit field presence.

Editions replace this with `edition = "2023"`, where each behavioral difference becomes a
separately toggleable **feature**. An edition defines a default set of features, but
individual files or fields can override specific features. This makes protobuf evolution
incremental rather than revolutionary.

For an IDE plugin, this creates a challenge: validation rules that were hardcoded per syntax
version ("proto3 fields must not have labels") now depend on a *combination* of feature
values that can vary per element. The plugin needs a model flexible enough to express this.

## Design Decisions

### Decision 1: Feature-Set Model

**Choice:** Represent edition differences as a `ProtobufFeature` data class with six
independently-togglable properties:

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

**Why features instead of version flags?** Because version flags create combinatorial
explosion. With `if (proto3)` guards scattered through the codebase, adding a new edition
means auditing every guard to decide which behavior applies. With features, each validation
rule checks exactly the property it cares about:

- "Can this field omit a label?" → check `fieldPresence`
- "Must the first enum value be zero?" → check `enumType`
- "Is `required` allowed?" → check `fieldPresence == LEGACY_REQUIRED`

Each feature maps to the six dimensions that actually changed between proto2, proto3, and
edition 2023 — matching the official protobuf feature specification.

### Decision 2: Backward-Compatible Feature Defaults

**Choice:** Define PROTO2 and PROTO3 as concrete `ProtobufFeature` instances alongside
EDITION_2023, rather than treating them as special cases:

```
PROTO2:        CLOSED enums, EXPLICIT presence, LEGACY_BEST_EFFORT JSON, EXPANDED repeated, no UTF-8 check
PROTO3:        OPEN enums,   IMPLICIT presence, ALLOW JSON,              PACKED repeated,   VERIFY UTF-8
EDITION_2023:  OPEN enums,   EXPLICIT presence, ALLOW JSON,              PACKED repeated,   VERIFY UTF-8
```

**Why this unifies validation:** Instead of maintaining three separate code paths
(`Protobuf2Annotator`, `Protobuf3Annotator`, `ProtobufEditionAnnotator`), the *intended*
architecture resolves any file — whether `syntax = "proto2"`, `syntax = "proto3"`, or
`edition = "2023"` — into a `ProtobufFeature` instance, then runs a single set of
feature-driven validation rules.

This makes EDITION_2023 expressible as "mostly proto3, but with explicit field presence" —
which is exactly what it is. Future editions (2024, 2025) would add new feature-set
constants without touching validation logic.

### Decision 3: Intentionally Incremental Implementation

**Choice:** The feature-set model is **defined but not yet wired into validation**. This
is deliberate scaffolding, not abandoned code.

**Current state of implementation:**

| Component | Status | Detail |
|---|---|---|
| Grammar (`EditionStatement`) | ✅ Complete | Parses `edition = "2023";` alongside `syntax = "..."` |
| PSI model | ✅ Complete | `ProtobufEditionStatementImpl` generated, `file.edition()` API works |
| Feature-set definitions | ✅ Complete | `PROTO2`, `PROTO3`, `EDITION_2023` constants defined |
| Feature enums | ✅ Complete | Six enum types with all valid values |
| `ProtobufEditionAnnotator` | ⚠️ Stub | Class exists but returns early; **not registered** in plugin.xml |
| Feature-driven validation | ❌ Not started | Validation still uses syntax-based annotators |

**Why ship the model before the migration?** Because the model is the hard design work.
The current annotators (`Protobuf2Annotator`, `Protobuf3Annotator`) are production-tested
and correct. Migrating them to feature-driven logic is a refactoring task, not a design
task. Having the feature-set model in place means:

- Edition files parse correctly and don't produce spurious errors (the edition annotator
  returns early rather than applying proto2/proto3 rules)
- The migration path is clear: replace `if (syntax == "proto3")` checks with
  `if (features.enumType == OPEN)` checks
- New editions can be added by defining a feature-set constant, even before validation
  is fully migrated

**What's explicitly skipped:** Per-element feature overrides (where a single field opts
into a different feature value than its file's default). This is a proto editions feature
that adds significant complexity and can be added after the base migration.

## How It Fits Together

**Current architecture (syntax-based):**
```
file.syntax() == "proto3"  →  Protobuf3Annotator (hardcoded proto3 rules)
file.syntax() == "proto2"  →  Protobuf2Annotator (hardcoded proto2 rules)
file.edition() != null     →  ProtobufEditionAnnotator (returns early, no validation)
```

**Target architecture (feature-based):**
```
file.syntax() == "proto3"  →  resolve to ProtobufFeature.PROTO3    ─┐
file.syntax() == "proto2"  →  resolve to ProtobufFeature.PROTO2    ─┼→ Unified feature-driven validator
file.edition() == "2023"   →  resolve to ProtobufFeature.EDITION_2023 ─┘
```

The grammar and PSI layer already treat `EditionStatement` and `SyntaxStatement` as
mutually exclusive alternatives at the file level. The file API exposes both `syntax()` and
`edition()` methods, and exactly one will return non-null. The feature-set model sits ready
to bridge this into a single validation path.

## Key Insight

The editions design mirrors protobuf's own evolution philosophy: make the new model
*subsume* the old one rather than replacing it. By expressing proto2 and proto3 as
feature-set instances, the plugin avoids a "legacy vs modern" split in its validation code.
The current state — model defined, migration pending — is a pragmatic choice: the
syntax-based annotators work correctly today, and the feature model is in place for when
editions adoption requires the unified path.
