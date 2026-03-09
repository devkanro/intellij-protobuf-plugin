# Brainstorm

A single-file state machine for managing design discussions before implementation.

Every branch has at most one active brainstorm, tracked entirely in `.copilot/brainstorm.md`. The file is both the state record and the design document — no external database, no hidden state. Anyone can open the file and see exactly where the discussion stands.

## When to Use

- User starts a new feature discussion or says "let's design..."
- User asks to create, edit, or manage `.copilot/brainstorm.md`
- Need to check brainstorm status or transition state
- User asks about the quick fix path or whether something needs a brainstorm
- User says "pause" or "resume" for an active brainstorm

## Quick Fix Path

Not every change needs a brainstorm. The whole point of this workflow is to catch design complexity early — trivial changes should flow freely.

**Skip the brainstorm for:**
- Bug fixes with obvious solutions (typos, off-by-one, null checks)
- Documentation-only changes (README, comments, CHANGELOG)
- Dependency version bumps
- Configuration adjustments (CI, linting rules)
- Pure refactoring that preserves behavior (rename, extract method)
- Isolated additions (a new test, a log line)

**A brainstorm IS needed for:**
- New features or capabilities
- API or interface changes
- Architecture or structural changes
- Changes affecting multiple components
- Changes with unclear scope or multiple valid approaches
- Performance optimizations involving tradeoffs

If a user asks "does this need a brainstorm?" or "can I just quick-fix this?", evaluate against the lists above. If it's borderline, suggest a lightweight brainstorm — just the Problem and Approach sections, skip the detailed design.

## State Machine

### States

| Status | Meaning | Allowed Actions |
|---|---|---|
| Planning | Discussing, design not finalized | Only modify brainstorm.md |
| Questioning | AI has questions for user | Only modify brainstorm.md |
| Ready | Design complete, awaiting confirmation | Only modify brainstorm.md |
| Implementing | Coding in progress | Modify code, use todos |
| Completed | Done, awaiting review | Only modify docs |
| Paused | Work temporarily suspended | No modifications |

### Transitions

```
Planning → Questioning    (AI identifies open questions)
Planning → Ready          (Design feels complete)
Planning → Paused         (User says "pause")

Questioning → Planning    (User answers questions)
Questioning → Paused      (User says "pause")

Ready → Implementing      (User confirms "let's build" / "start")
Ready → Planning          (User wants to reconsider)
Ready → Paused            (User says "pause")

Implementing → Completed  (All tasks done)
Implementing → Paused     (User says "pause")

Paused → {previous state} (User says "resume")

Completed → (terminal)
```

### Rules

1. **Never skip states.** Cannot go from Planning directly to Implementing — the design must pass through Ready first, because that confirmation step is where the user commits to a specific plan.
2. **No code changes until Implementing.** During Planning/Questioning/Ready, only `.copilot/brainstorm.md` may be modified. This prevents premature coding that might need to be thrown away.
3. **Paused remembers.** Record the previous state in `paused_from` so resuming returns to exactly where things left off.
4. **Completed is near-terminal.** Only documentation changes are allowed after completion.

## File Format

`.copilot/brainstorm.md` uses YAML frontmatter for machine-readable state, followed by markdown sections for human-readable content:

```markdown
---
status: Planning
created: 2024-01-15
updated: 2024-01-15
paused_from:
---

# Feature: [Title]

## Problem
[What problem are we solving? Why does it matter?]

## Approach
[High-level approach to solving the problem]

## Design
[Detailed design: data structures, API shapes, component interactions]

## Questions
[Open questions that need answers before proceeding]
- [ ] Question 1
- [ ] Question 2

## Decisions
[Key decisions made during discussion, with rationale]
- **Decision 1**: [choice] — because [reason]

## Tasks
[Implementation tasks, populated when entering Implementing state]
- [ ] Task 1
- [ ] Task 2

## Discussion Log
[Each round of discussion is recorded here as a timestamped summary]

### Round 1 — [date]
**Topic**: [What was discussed]
**Key points**: [Bullet summary of ideas, concerns raised]
**Outcome**: [What was decided or what remains open]

### Round 2 — [date]
...
```

## Workflow

### Starting a New Brainstorm

1. Check if `.copilot/brainstorm.md` already exists on this branch
   - If it exists and is active (not Completed), warn the user and ask how to proceed
   - If it exists and is Completed, archive or overwrite based on user preference
2. Create `.github/` directory if needed
3. Create `.copilot/brainstorm.md` with status `Planning`
4. Fill in the Problem section based on the user's description
5. Begin the design discussion

### During Planning

Discuss the design with the user. Update the Approach and Design sections as ideas develop. Think about edge cases, integration points, and potential issues.

**After each round of discussion**, append a timestamped summary to the Discussion Log section. A "round" is a coherent exchange on a topic — it ends when the conversation shifts direction, the user pauses, or a decision is reached. Keep summaries concise but capture the key points and outcomes.

If you identify questions the user needs to answer — things that would meaningfully change the implementation — transition to `Questioning` and list them clearly.

When the design feels complete and you're confident about the approach, transition to `Ready` and present a concise summary of the finalized plan.

### During Questioning

List specific, actionable questions in the Questions section. Each question should explain why the answer matters for the design.

Once the user answers, check off questions, incorporate answers into the Design section, and transition back to `Planning` to continue refining.

### During Ready

Present a summary of the finalized design. Ask the user to confirm: "Ready to implement?"

- If confirmed → transition to `Implementing`, populate the Tasks section with concrete implementation steps
- If the user has second thoughts → transition back to `Planning`

### During Implementing

- Create todos from the Tasks section for tracking
- Track progress by checking off tasks in brainstorm.md
- Code changes are now allowed
- When all tasks are complete, transition to `Completed`

### Pausing and Resuming

- **Pause**: Set status to `Paused`, save current state in `paused_from`, update the timestamp
- **Resume**: Restore status from `paused_from`, clear `paused_from`, update the timestamp

### Completion

When status is `Completed`, the feature implementation is done. The brainstorm file remains as a design record. Only documentation updates are allowed at this point.

## Status Check

When asked about brainstorm status, read `.copilot/brainstorm.md` and report:
- Current state
- Brief summary of the design
- Open questions (if any)
- Task progress (if Implementing)
- How long it's been since the last update

If no brainstorm file exists, say so and ask if they'd like to start one.
