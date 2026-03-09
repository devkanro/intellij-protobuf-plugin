# Copilot Instructions

This project uses a structured Copilot workflow with custom skills. Read this before doing anything.

## Workflow Cycle

```
brainstorm → implement → ship → reflect
     ↑                            │
     └────────────────────────────┘
          knowledge feeds back
```

Every non-trivial change follows this cycle:

1. **Brainstorm** — Design the solution in `.copilot/brainstorm.md` before writing code. The brainstorm skill manages a state machine (Planning → Questioning → Ready → Implementing → Completed) that prevents premature coding.

2. **Implement** — Write code only after the brainstorm reaches `Implementing` state. Use todos to track progress.

3. **Ship** — Use the ship skill (not raw git commands) to commit and push. It enforces build verification, test execution, and brainstorm status checks before allowing a commit.

4. **Reflect** — After every ship, reflect on what happened. Learnings are captured in `.copilot/knowledge.md` and feed back into future work.

Trivial changes (bug fixes, typos, config tweaks) skip the brainstorm and go straight to ship.

## Key Files

| File | Purpose |
|---|---|
| `.copilot/brainstorm.md` | Active design discussion (one per branch) |
| `.copilot/knowledge.md` | Accumulated learnings and patterns |
| `.copilot/docmap.md` | Documentation structure and code-to-doc mapping |
| `.github/copilot-instructions.md` | This file — workflow overview |
| `.github/instructions/` | Custom skill definitions |

## Knowledge Base

`.copilot/knowledge.md` is a living document that grows with each reflection. It contains:

- **Project Patterns** — Recurring patterns specific to this codebase
- **Common Pitfalls** — Mistakes to avoid (learned the hard way)
- **Workflow Learnings** — What works and what doesn't in our process
- **Reflections** — Individual post-ship retrospectives

Before starting new work, read the knowledge base. Past reflections often contain insights that save time — similar problems encountered before, approaches that worked or didn't, and codebase quirks to watch out for.

## Skills

| Skill | Purpose |
|---|---|
| `brainstorm` | Design discussions via `.copilot/brainstorm.md` state machine |
| `ship` | Build → test → commit → push with safety checks |
| `document` | Documentation management, code-to-doc mapping |
| `reflect` | Post-ship reflection, knowledge base maintenance |

## Rules

1. **No code changes during design** — Until brainstorm reaches `Implementing`, only `.copilot/brainstorm.md` may be modified
2. **Always ship through the ship workflow** — Don't use raw git commands for committing; the ship skill enforces quality gates
3. **Reflect after every ship** — Even quick fixes deserve a brief reflection
4. **Ask before changing skills** — The reflect workflow may propose workflow improvements, but always gets user approval first
5. **Keep knowledge current** — Update `.copilot/knowledge.md` after each reflection, curate patterns when they recur
