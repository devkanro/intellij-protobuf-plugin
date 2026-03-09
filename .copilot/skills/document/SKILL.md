---
name: document
description: >-
  Documentation management skill. Use when user asks to find, create, update,
  or organize documentation. Also use when code changes may require doc updates,
  when user asks "where is this documented?", "update the docs", or needs to
  understand the documentation structure. All documentation operations should
  go through this skill. Triggers on mentions of README, CHANGELOG, docs,
  documentation, or doc-related file paths.
---

# Document

Manages the project's documentation structure, maintains a code-to-doc mapping, and ensures docs stay in sync with code changes.

Documentation is only useful if it's findable and current. This skill maintains a living map of what's documented where, so you never have to grep through files wondering "is this documented anywhere?" It also ensures that code changes trigger the right doc updates.

## When to Use

- User asks to find, create, or update documentation
- Code changes affect documented behavior (API, configuration, usage)
- User asks "where is this documented?" or "what docs do we have?"
- User mentions README, CHANGELOG, or specific doc files
- After implementing a feature, to update related docs
- User wants to understand the documentation structure

## Documentation Map

Maintain a documentation map in `.copilot/docmap.md` that tracks:

```markdown
---
updated: 2024-01-15
---

# Documentation Map

## Files

| File | Purpose | Related Code |
|---|---|---|
| README.md | Project overview, setup, usage | build.gradle.kts, src/ |
| CHANGELOG.md | Version history, release notes | All changes |
| CODE_OF_CONDUCT.md | Community guidelines | — |
| .copilot/brainstorm.md | Active design discussion | Current feature |

## Sections Index

### README.md
- Installation → build.gradle.kts, gradle.properties
- Usage → src/main/kotlin/...
- Configuration → resources/...
- Contributing → .github/

### CHANGELOG.md
- [Unreleased] → current branch changes
- [x.y.z] → tagged releases
```

## Workflows

### Finding Documentation

When user asks "where is X documented?" or "is there docs for Y?":

1. Read `.copilot/docmap.md` for the mapping
2. Search documentation files for relevant content
3. Report findings with file paths and line numbers
4. If not documented, suggest where it should be added

### Creating Documentation

When adding new documentation:

1. Determine the right location based on the doc map
2. Follow the existing style and format of nearby docs
3. Update `.copilot/docmap.md` with the new entry
4. Cross-reference related code paths

### Updating Documentation

When code changes affect documented behavior:

1. Identify which docs are affected using the doc map's "Related Code" column
2. Read the current documentation
3. Update to reflect the new behavior
4. Update the `updated` timestamp in docmap.md
5. If the change is user-facing, add a CHANGELOG entry under `[Unreleased]`

### CHANGELOG Management

Follow [Keep a Changelog](https://keepachangelog.com/) format:

```markdown
## [Unreleased]

### Added
- New feature description

### Changed
- Modified behavior description

### Fixed
- Bug fix description

### Removed
- Removed feature description
```

Categories: Added, Changed, Deprecated, Removed, Fixed, Security.

### Doc Health Check

When asked to review documentation health:

1. Scan all doc files for staleness (compare against recent code changes)
2. Check for broken internal references
3. Identify undocumented public APIs or features
4. Report findings with suggested actions

## Conventions

- **Language**: Match the existing project language (this project uses English)
- **Style**: Be concise and direct. Prefer examples over lengthy explanations.
- **Structure**: Use headings, tables, and code blocks for scannability
- **Links**: Use relative links between project files
- **Freshness**: Always update the `updated` date in docmap.md when making changes

## Bootstrap

If `.copilot/docmap.md` doesn't exist yet, create it by scanning the project:

1. Find all `.md` files
2. Read each file's purpose from its content
3. Map code relationships based on file references
4. Generate the initial docmap
