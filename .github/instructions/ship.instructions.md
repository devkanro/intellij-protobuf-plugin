# Ship

A disciplined shipping workflow that ensures quality before code leaves the branch.

Shipping is not just `git push`. It's a checklist: build passes, tests pass, the brainstorm state is correct, the commit message is meaningful, and the code is pushed cleanly. This skill enforces that checklist every time.

## When to Use

- User says "ship", "commit", "push", "let's ship it", "we're done"
- User wants to finalize current work and push to remote
- User asks to create a commit for recent changes

## Pre-Ship Checklist

Before any commit, run through these checks **in order**. Stop at the first failure and report it to the user.

### 1. Brainstorm Status Check

Read `.copilot/brainstorm.md` if it exists:

| Brainstorm Status | Ship Allowed? | Action |
|---|---|---|
| Planning | ❌ No | Design not finalized — finish the brainstorm first |
| Questioning | ❌ No | Open questions remain — answer them first |
| Ready | ❌ No | Design confirmed but not started — begin implementing first |
| Implementing | ⚠️ Partial | Allow shipping intermediate progress with a WIP commit |
| Completed | ✅ Yes | Full ship allowed |
| Paused | ❌ No | Work is paused — resume or complete first |
| No file | ✅ Yes | No brainstorm needed (quick fix path) |

For WIP commits during Implementing:
- Commit message must start with `WIP:` prefix
- Remind the user that the brainstorm is still in progress

### 2. Build Verification

Run the project build:
```
./gradlew build
```

- If build fails → stop and report errors
- If build succeeds → continue

### 3. Test Verification

Tests are run as part of the Gradle build. If any tests fail:
- Report which tests failed
- Do not proceed with commit

### 4. Uncommitted Changes Review

Run `git status` and `git diff --stat` to show the user what will be committed:
- List all modified, added, and deleted files
- Show a brief summary of changes
- Ask user to confirm the changes look correct

## Commit Process

### Commit Message Format

```
<type>: <concise description>

<optional body explaining what and why>

Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>
```

**Types:**
- `feat` — new feature or capability
- `fix` — bug fix
- `refactor` — code restructuring without behavior change
- `docs` — documentation only
- `chore` — build, CI, dependencies
- `test` — adding or updating tests

Generate the commit message based on the actual changes. Present it to the user for approval before committing.

### Git Operations

1. `git add` — stage relevant files (not blindly `git add .`; be intentional)
2. `git commit` — with the approved message
3. `git push` — push to the current branch's remote

If push fails due to diverged history, inform the user and suggest options (pull --rebase, force push, etc.) rather than acting unilaterally.

## Post-Ship

After a successful ship:

1. If brainstorm status was `Completed`, note that the feature is shipped
2. **Trigger the reflect skill** — remind the user (or automatically initiate) a reflection on what was shipped
3. Report success with a summary: branch name, commit hash, files changed

## Error Recovery

- **Build failure**: Show the error output, suggest fixes
- **Test failure**: Show failing tests, suggest investigation
- **Merge conflict**: Explain the conflict, do not auto-resolve
- **Push rejection**: Explain why (usually needs pull), suggest safe resolution
- **Network issues**: Retry once, then report

## Quick Ship

For trivial changes (typos, config tweaks) where no brainstorm exists, the flow is streamlined:
1. Build check
2. Show changes
3. Commit and push

No brainstorm check needed, no WIP prefix — just a clean commit.
