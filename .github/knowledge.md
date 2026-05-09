---
updated: 2026-05-09
---

# Knowledge Base

Accumulated learnings from this project. Read this before starting new work.

## Project Patterns

- IntelliJ Platform Gradle Plugin upgrades can require both Gradle wrapper updates and build script API adjustments.
- Grammar/parser generation depends on IntelliJ Platform classpath alignment; when `generateParser` fails with missing IntelliJ classes, check Grammar-Kit and IntelliJ Platform Gradle Plugin compatibility together.

## Common Pitfalls

- `README.md` must contain `<!-- Plugin description -->` and `<!-- Plugin description end -->` markers because `patchPluginXml` extracts the plugin description from that section.
- Local Gradle project caches under `.gradle` can fail on Windows with file-lock or directory-creation errors; validating in a clean temporary workspace can distinguish local cache problems from source/build-script problems.

## Workflow Learnings

- For build-tooling updates, verify plugin portal versions first, then run a clean build after each compatibility-driven change.
- When a standard workspace build is blocked by local cache permissions, document the workaround and validation command in the PR.

## Reflections

### 2026-05-09 — Build plugin update for 2026.1

**What was done**: Updated the build tooling for the 2026.1 target by upgrading the IntelliJ Platform Gradle Plugin, Grammar-Kit plugin, and Gradle wrapper. Adapted the `create()` dependency declaration to the newer API and restored README plugin-description markers required by `patchPluginXml`.

**What went well**:
- Updating Grammar-Kit and IntelliJ Platform Gradle Plugin together resolved the original `generateParser` classpath failure.
- A clean temporary workspace build isolated source/build-script issues from local `.gradle` cache permission problems.
- The final build passed after adapting the dependency declaration and restoring README markers.

**What was harder than expected**:
- IntelliJ Platform Gradle Plugin `2.16.0` required Gradle `9.0.0`, which introduced an API compatibility adjustment in `build.gradle.kts`.
- The local workspace Gradle cache repeatedly failed with Windows file-lock/directory errors, so standard in-place build verification was not reliable.
- `patchPluginXml` failed late because the README description markers were missing.

**Patterns noticed**:
- Build-tooling upgrades often reveal chained compatibility requirements: Gradle plugin version, Gradle wrapper version, DSL API, and documentation-derived metadata all need to line up.
- Local environment failures should be separated from source failures by using a clean copy and explicit temporary cache directories.

**Workflow observations**:
- The ship flow caught the local standard-build failure, but the clean-workspace validation provided enough confidence to proceed with a documented PR note.
- This was a quick build-tooling fix without a dedicated brainstorm; future non-trivial version migrations should start with a brief brainstorm entry before edits.
