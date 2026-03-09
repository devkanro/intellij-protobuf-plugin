# Annotation System

## The Problem

Validating Protocol Buffers is deceptively complex because protobuf is not one language — it
is three divergent dialects sharing one syntax. Proto2 *requires* field labels (`required`,
`optional`, `repeated`); proto3 *forbids* `required` and treats `optional` as a presence
signal. Proto3 bans groups, extensions, weak imports, and default values — all legal in
proto2. Editions introduce a feature-flag model that decouples semantics from syntax version
entirely.

A single monolithic validator cannot cleanly express "this rule applies only when the file
says `syntax = "proto3"`" without devolving into a tangle of conditionals. Meanwhile, the
rules that *are* universal — field number uniqueness, naming conventions, symbol resolution,
map key type constraints — must run regardless of dialect. The design challenge is separating
version-specific rules from universal rules without duplicating logic or creating coupling
between the two layers.

Beyond structural validation, errors must be actionable. An unresolved type name is
frustrating; an unresolved type name with a one-click "Add import" fix is productive. The
annotation system must pair diagnostics with repairs wherever possible.

## Design Decisions

### Decision 1: Per-Version Annotators (Layered Validation)

**Why not one big annotator?** Because version-specific rules are *mutually exclusive* and
change independently. Proto3's "enums must have a zero value" rule has no proto2 equivalent.
Proto2's "fields must have a label" rule is the opposite of proto3's behavior. Mixing these
in one class means every rule needs a `when (syntax)` guard, and adding edition support would
fork every branch into three.

The chosen design uses a **layered architecture**:

| Layer | Class | Activates When |
|-------|-------|----------------|
| Universal | `ProtobufAnnotator` | Always (all `.proto` files) |
| Proto2 | `Protobuf2Annotator` | `syntax` is `"proto2"` or unspecified |
| Proto3 | `Protobuf3Annotator` | `syntax` is exactly `"proto3"` |
| Editions | `ProtobufEditionAnnotator` | `edition()` is non-null |

Each version annotator **self-gates** with an early return in `annotate()`. IntelliJ invokes
all registered annotators on every PSI element, so gating is cheap — a single `file.syntax()`
call. The universal annotator has no gate and runs unconditionally.

**Maintenance payoff:** Adding edition-specific validation means filling in
`ProtobufEditionAnnotator` (currently a stub) without touching proto2 or proto3 logic. The
`ProtobufFeature` data class already defines feature flags for `EDITION_2023` — the annotator
just needs to enforce them. No existing code changes.

### Decision 2: Annotators Over Inspections

IntelliJ offers two validation mechanisms: **Annotators** (real-time, not suppressible) and
**Inspections** (configurable, suppressible, batchable). This plugin uses annotators for
nearly all validation. Why?

**Protobuf errors are not opinions.** An unresolved symbol, a duplicate field number, or a
missing `required` label in proto2 are specification violations — `protoc` will reject the
file. Making these suppressible via inspections would let users silence real errors, creating
a false sense of correctness. Annotators are the right tool because they mirror the compiler:
always on, not negotiable.

**Real-time feedback matters for protobuf.** Protobuf files are schema definitions consumed
by code generators. A typo in a field type doesn't cause a runtime exception you can debug —
it causes a build failure in a generated language you may not be reading. Instant red
underlines as you type catch errors before the costly `protoc` round-trip.

**The tradeoff:** Naming convention warnings (e.g., `snake_case` for fields) *are*
style-oriented and arguably belong in inspections. The current design uses `WEAK_WARNING`
severity for these, which keeps them visible but unobtrusive — a pragmatic middle ground that
avoids splitting the validation infrastructure across two frameworks.

### Decision 3: Quick Fixes as First-Class Citizens

Annotators do not just report problems — they attach actionable repairs via IntelliJ's
`withFix()` API. This is a deliberate design choice, not a convenience feature.

**AddImportFix — the critical path.** When `ProtobufAnnotator` encounters an unresolved type
name, it creates an `ERROR` annotation with an `AddImportFix` attached. The fix searches
the stub index by short name, filters candidates by qualified name suffix, and — if one
match exists — inserts the import directly. For multiple matches, it presents a popup. This
implements `HintAction`, so the fix appears as an inline suggestion without requiring
Alt+Enter. The entire flow (detect → search → import → update reference) happens without
leaving the editor.

**RenameFix — safe refactoring over manual edits.** Naming convention violations attach a
`RenameFix` that delegates to IntelliJ's `RefactoringFactory.createRename()`. This ensures
all references update atomically — critical because protobuf names propagate into generated
code across multiple languages.

**OptimizeImportsFix — batch cleanup.** Unused import warnings (from `FileTracker`) attach
an `OptimizeImportsFix` that removes all unused imports in one action, matching the
"Optimize Imports" idiom familiar from Java/Kotlin development.

**Why pair instead of separate?** Annotations without fixes are complaints. Annotations with
fixes are workflows. By co-locating detection and repair, the code that best understands the
problem also provides the solution — no indirection through a separate fix-registration
system.

## How It Fits Together

```
                     IntelliJ Annotation Framework
                     (invokes all registered annotators per PSI element)
                                    │
                 ┌──────────────────┼──────────────────┐
                 │                  │                   │
        ProtobufAnnotator   Protobuf3Annotator   Protobuf2Annotator
        (universal rules)   (proto3-only rules)  (proto2-only rules)
                 │
      ┌──────────┼──────────┐
      │          │          │
  FileTracker NumberTracker ScopeTracker
  (imports)  (field nums)  (name uniqueness)
```

**Trackers are cached utilities, not annotators.** `FileTracker`, `NumberTracker`, and
`ScopeTracker` use `CachedValuesManager` to compute constraint sets once per PSI
modification, then individual `visit()` calls check a single element against the cached set.
This avoids O(n²) recomputation — the tracker records all items in `init`, and each
annotator visit is O(1) lookup against the cached data.

**The record-visit pattern:** Each tracker records all definitions in a scope during
construction (`init { scope.items().forEach { record(it) } }`), then exposes `visit(element,
holder)` methods that check one element against the recorded set. The annotator delegates to
the tracker per-element; the tracker holds the cross-element knowledge.

## Key Insight

The annotation system's architecture reflects a fundamental property of the protobuf
specification: most rules are universal, but the rules that differ between versions are
*contradictory*, not just additive. Proto2 requires what proto3 forbids. You cannot express
this as feature flags on a single validator without the logic becoming "if proto2 do X, else
if proto3 do the opposite of X." Separate annotators make each version's rules self-contained
and independently testable — and they make the edition annotator a clean extension point
rather than another branch in an existing conditional tree.
