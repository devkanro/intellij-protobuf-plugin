---
status: Questioning
created: 2026-03-09
updated: 2026-03-09
paused_from:
---

# Feature: Deep-Dive Documentation for Plugin Internals

## Problem

The existing docs (`docs/architecture.md`, `docs/development.md`, `docs/extension-points.md`, `docs/modules/`) provide a good bird's-eye view — what exists and where — but lack the depth needed to understand *how* things work. Key subsystems like symbol resolution, stub indexing, and the PSI mixin pattern are mentioned only briefly. A new contributor or future-us would struggle to trace a symbol resolve call or understand how stubs are built without reading dozens of source files.

## Approach

Create focused, single-topic documentation files for each major subsystem. Each doc should explain the design, walk through the key code paths, and provide enough context that a reader can navigate the subsystem confidently. Prioritize the areas that are most complex and least documented.

## Design

### Proposed Documentation Files

Based on codebase analysis (~352 Kotlin files, 2 BNF grammars), here are the candidates grouped by priority:

#### Tier 1 — Core Internals (Very High Priority)

| Doc | Covers | Key Classes |
|-----|--------|-------------|
| `docs/internals/psi-hierarchy.md` | PSI element types, mixin pattern, feature interfaces, BNF-to-PSI generation | `ProtobufElement`, `ProtobufElementBase`, `*Mixin.kt`, feature interfaces |
| `docs/internals/symbol-resolution.md` | Name resolution pipeline, scope system, cross-file imports, public imports | `ProtobufSymbolResolver`, `ProtobufScope`, 6 proto + 5 textproto reference classes |
| `docs/internals/stub-indexing.md` | Stub types, indices, serialization, external data, querying | 11 stub types, `ShortNameIndex`, `QualifiedNameIndex`, `ResourceTypeIndex` |

#### Tier 2 — Important Subsystems (High Priority)

| Doc | Covers | Key Classes |
|-----|--------|-------------|
| `docs/internals/annotation-system.md` | Annotators, version-specific validation, quick fixes, error severity | `ProtobufAnnotator`, `Protobuf2/3Annotator`, `ProtobufEditionAnnotator`, 5 quick fixes |
| `docs/internals/code-completion.md` | Completion contributors, providers, insert handlers, context detection | `ProtobufCompletionContributor`, 9 providers, 4 insert handlers |
| `docs/internals/compiler-system.md` | Internal protoc, compiler plugins, state machine, descriptor generation | `Protoc`, `ProtobufCompilerPlugin`, `States.kt`, 10 compiler classes |

#### Tier 3 — Specialized Topics (Medium Priority)

| Doc | Covers | Key Classes |
|-----|--------|-------------|
| `docs/internals/prototext-support.md` | Text format language, header comments, schema-based validation | `prototext.bnf`, `ProtoTextFile`, 5 reference classes, `ProtoTextAnnotator` |
| `docs/internals/edition-support.md` | Editions vs syntax versions, feature sets, current implementation status | `ProtobufFeature`, `ProtobufEditionAnnotator`, `EditionStatement` |

### Directory Structure

```
docs/
├── architecture.md          (existing — high-level overview)
├── development.md           (existing — build & dev guide)
├── extension-points.md      (existing — public API)
├── modules/                 (existing — per-module docs)
│   ├── java.md
│   ├── go.md
│   ├── grpc.md
│   ├── aip.md
│   └── sisyphus.md
└── internals/               (NEW — deep-dive subsystem docs)
    ├── psi-hierarchy.md
    ├── symbol-resolution.md
    ├── stub-indexing.md
    ├── annotation-system.md
    ├── code-completion.md
    ├── compiler-system.md
    ├── prototext-support.md
    └── edition-support.md
```

## Questions

Open questions that affect scope and approach:

- [ ] **Scope**: Should we do all 8 docs in one pass, or start with Tier 1 (3 docs) and iterate?
- [ ] **Depth vs. breadth**: Should each doc include code walkthroughs (e.g., "trace a symbol resolve from user click to result"), or stay at the design/architecture level?
- [ ] **Audience**: Are these docs primarily for contributors to this plugin, or also for users who want to build extensions on top of it?
- [ ] **Formatting/navigation docs**: The formatter and navigation (go-to-symbol, find-usages, rename) are medium-complexity. Worth separate docs or fold into existing `architecture.md`?

## Decisions

(Pending answers to questions above)

## Tasks

(Populated when entering Implementing state)

## Discussion Log

### Round 1 — 2026-03-09
**Topic**: Initial brainstorm — what documentation to create
**Key points**:
- Codebase analysis identified 10 major subsystems, of which 7+ have thin/missing documentation
- Existing docs cover the "what" well but lack the "how" — PSI mixin pattern, symbol resolution pipeline, stub lifecycle are barely explained
- Proposed 8 new docs organized under `docs/internals/` in 3 priority tiers
- Extension points doc already exists but could be enhanced with implementation walkthroughs
**Outcome**: Proposed plan needs user input on scope, depth, and audience before finalizing
