# Reflect

Captures lessons learned after each ship and feeds them back into the workflow.

The reflect skill closes the feedback loop. After shipping code, it asks: what went well, what was harder than expected, what patterns emerged? These observations accumulate in `.copilot/knowledge.md` — a living knowledge base that makes every future task a little smoother.

## When to Use

- After every successful ship (triggered by the ship skill)
- User says "reflect", "what did we learn", "retrospective"
- User asks to review or search the knowledge base
- User wants to improve the workflow or skills

## Reflection Process

### 1. Gather Context

Read the following to understand what just happened:

- `git log` — recent commits on this branch
- `git diff` — what changed (compare with main/master)
- `.copilot/brainstorm.md` — the design discussion (if exists)
- Changed files — scope and nature of the work

### 2. Generate Reflection

Produce a structured reflection covering:

```markdown
## [date] — [brief title of what was shipped]

**What was done**: [1-2 sentence summary of the change]

**What went well**:
- [Things that worked smoothly]

**What was harder than expected**:
- [Unexpected challenges, detours, or mistakes]

**Patterns noticed**:
- [Recurring themes, common pitfalls, useful techniques]

**Workflow observations**:
- [How the brainstorm→implement→ship flow worked for this change]
- [Any friction points in the process]
```

Present the reflection to the user for review before saving.

### 3. Update Knowledge Base

Append the reflection to `.copilot/knowledge.md`. The file structure:

```markdown
---
updated: 2024-01-15
---

# Knowledge Base

Accumulated learnings from this project. Read this before starting new work.

## Project Patterns
[Recurring patterns specific to this codebase]

## Common Pitfalls
[Things that have gone wrong before — avoid repeating]

## Workflow Learnings
[What works well, what doesn't in our development process]

## Reflections

### [date] — [title]
[Full reflection entry]

### [date] — [title]
[Full reflection entry]
```

The top sections (Project Patterns, Common Pitfalls, Workflow Learnings) are **curated summaries** distilled from individual reflections. Update them when a pattern appears across multiple reflections.

### 4. Propose Workflow Improvements

Based on the reflection, consider whether any skills should be updated:

- **New skill needed?** — If a recurring task pattern emerged that isn't covered
- **Skill update needed?** — If a current skill's workflow was friction-heavy
- **Instruction update needed?** — If the overall workflow needs adjustment

**Important**: Never modify skills without user approval. Present proposals like:

> Based on this reflection, I notice [pattern]. Would you like me to update the [skill name] skill to [proposed change]?

Wait for explicit confirmation before making any skill changes.

## Knowledge Base Operations

### Searching Knowledge

When user asks "have we seen this before?" or "what do we know about X?":

1. Read `.copilot/knowledge.md`
2. Search for relevant entries in both curated sections and individual reflections
3. Report findings with context

### Curating Knowledge

Periodically (or when asked), review the reflections and:

1. Extract recurring patterns into the Project Patterns section
2. Extract repeated mistakes into Common Pitfalls
3. Extract workflow insights into Workflow Learnings
4. Keep individual reflections as the detailed record

## Bootstrap

If `.copilot/knowledge.md` doesn't exist, create it with the template structure and the first reflection entry.

## Integration with Other Skills

- **Ship skill** triggers reflect after successful push
- **Brainstorm skill** can reference knowledge for past decisions on similar problems
- **Document skill** may be triggered if reflection reveals documentation gaps
