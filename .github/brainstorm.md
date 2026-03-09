---
status: Implementing
created: 2026-03-09
updated: 2026-03-09
paused_from:
---

# Feature: Documentation Overhaul — Structure, Design Docs, and README

## Problem

The existing docs describe *what* exists and *where* — class names, file locations, feature lists. But that information is already in the code. What's missing is the *why*: why was the mixin pattern chosen for PSI elements? Why does symbol resolution have separate absolute and relative phases? Why use a custom in-process compiler instead of calling protoc? These design decisions are invisible in code but critical for making good future decisions.

Additionally, the current docs structure is flat and unfriendly to newcomers — opening `architecture.md` immediately throws PSI hierarchies, stub indices, and compiler internals at the reader with no gradual on-ramp.

The README is also bare-bones: a checklist of features with no visual appeal or project identity.

## Approach

Create design-intent documentation that answers *why*, not *what*. Each doc should:

- **Explain the problem** the subsystem solves and why it's non-trivial
- **Describe the design choices** and what alternatives were considered or rejected
- **Reveal the tradeoffs** — what was gained, what was sacrificed
- **Connect decisions to constraints** — IntelliJ platform requirements, protobuf spec demands, performance needs

Reorganize the doc structure by reader journey (overview → architecture → design deep-dives) so newcomers aren't overwhelmed. Refresh the README for visual impact and add Copilot workflow section.

## Design

### New Documentation Structure

```
docs/
├── README.md                    ← Documentation index / navigation page
├── overview.md                  ← What the plugin does, user experience, key concepts
├── architecture.md              ← Simplified: layer diagram + one paragraph per layer
├── getting-started.md           ← Dev environment setup (extracted from development.md)
├── contributing.md              ← How to contribute: grammar, features, modules
├── extension-points.md          ← Extension API reference (existing, refined)
├── design/                      ← Deep-dive "why" documents
│   ├── psi-and-mixin.md         ← Why mixin pattern? Why feature interfaces?
│   ├── symbol-resolution.md     ← Why two-phase resolve? Why scope hierarchy?
│   ├── stub-indexing.md         ← Why stubs? What tradeoffs in index design?
│   ├── annotation-system.md     ← Why per-version annotators? Validation philosophy
│   ├── code-completion.md       ← Why provider-per-context? Insert handler design
│   ├── compiler-system.md       ← Why in-process compiler? Plugin architecture
│   ├── prototext.md             ← Why separate language? Schema linking design
│   └── editions.md              ← Why feature-set model? Migration from syntax versions
└── modules/                     ← Per-module integration docs (existing, preserved)
    ├── java.md
    ├── go.md
    ├── grpc.md
    ├── aip.md
    └── sisyphus.md
```

### Reader Journeys

- **"What does this do?"** → README → `overview.md`
- **"I want to contribute"** → `getting-started.md` → `architecture.md` → `design/*`
- **"I want to extend it"** → `extension-points.md` → `modules/*`
- **"Why was X designed this way?"** → `design/*` directly

### README Overhaul

Redesign `README.md` at project root:

1. **Hero section**: Logo + one-line tagline + badges (existing) + install button/link
2. **Feature highlights**: Visual cards/sections with screenshots inline, not a checklist
3. **Quick start**: 3-step install guide
4. **Compatibility warning**: Keep the JetBrains plugin conflict note but style it better
5. **Architecture at a glance**: Simple layer diagram or feature overview image
6. **Copilot workflow section**: New — explain that this project uses AI-assisted development with a structured brainstorm → implement → ship → reflect cycle. Link to `.github/copilot-instructions.md` and `.github/skills/`
7. **Contributing**: Link to `docs/contributing.md`
8. **Screenshots gallery**: Reorganize existing screenshots with better captions

### Design Doc Template

Each `docs/design/*.md` follows this structure:

```markdown
# [Subsystem Name]

## The Problem
What challenge does this subsystem address? Why isn't it trivial?

## Design Decisions
### Decision 1: [Choice made]
**Context**: What constraints or requirements drove this?
**Alternatives considered**: What else could have been done?
**Why this approach**: What made it the best fit?
**Tradeoffs**: What was sacrificed?

### Decision 2: ...

## How It Fits Together
Brief sketch of how this subsystem connects to others.
(Not a code walkthrough — just enough to navigate.)

## Key Insight
One or two sentences capturing the essential "aha" for this subsystem.
```

## Questions

- [x] **Scope**: All 8 design docs + restructure + README — *decided: do it all*
- [x] **Why vs. What**: Focus on design intent and rationale — *decided: why-first*
- [x] **Audience**: Contributors and future-us (including AI) — *decided*
- [x] **Formatting/navigation**: Fold into architecture.md, not separate docs — *decided*
- [x] **Doc structure**: Progressive journey (overview → architecture → design/) — *decided*
- [x] **README**: Visual refresh + Copilot workflow section — *decided*

## Decisions

1. **Why-first documentation**: Each doc focuses on design intent and rationale rather than describing current implementation. Code tells you *what*; docs tell you *why*.
2. **Progressive structure**: Docs organized by reader journey — newcomers start with overview, contributors drill into design/, extenders use extension-points.
3. **All 8 design docs in one pass**: Subsystems are interconnected; writing them together gives coherent coverage.
4. **Audience is contributors + AI**: Not extension developers (extension-points.md already serves them).
5. **Formatting/navigation merged into architecture.md**: Standard IntelliJ patterns, not enough unique "why" for standalone docs.
6. **README overhaul**: Visual refresh with feature highlights, quick start, and Copilot workflow section.

## Tasks

(Populated when entering Implementing state)

## Discussion Log

### Round 1 — 2026-03-09
**Topic**: Initial brainstorm — what documentation to create
**Key points**:
- Codebase analysis identified 10 major subsystems, of which 7+ have thin/missing documentation
- Existing docs cover the "what" well but lack the "how"
- Proposed 8 new docs organized under `docs/internals/` in 3 priority tiers
**Outcome**: Proposed plan needs user input on scope, depth, and audience

### Round 2 — 2026-03-09
**Topic**: Documentation philosophy — why vs. what
**Key points**:
- User pointed out docs should explain *why* (design intent) not *what* (current implementation)
- "What" is already in the code; "why" is the invisible knowledge that gets lost
- Docs become more stable across refactors because rationale outlives implementation
**Outcome**: Decided on why-first approach

### Round 3 — 2026-03-09
**Topic**: Scope, audience, and open questions
**Key points**:
- All 8 docs in one pass (subsystems are interconnected)
- Audience: contributors + future-us/AI (extension-points.md already serves extenders)
- Formatting/navigation: merge into architecture.md (standard IntelliJ patterns)
- Renamed `internals/` to `design/` to match why-first philosophy
**Outcome**: All open questions resolved

### Round 4 — 2026-03-09
**Topic**: Documentation structure reorganization + README
**Key points**:
- Current flat structure is unfriendly to newcomers
- Reorganize by reader journey: overview → architecture → design/
- Split development.md into getting-started.md + contributing.md
- README needs visual refresh: feature highlights instead of checklist, quick start, screenshots inline
- Add Copilot workflow section to README explaining brainstorm → implement → ship → reflect cycle
**Outcome**: Full scope finalized — ready to implement