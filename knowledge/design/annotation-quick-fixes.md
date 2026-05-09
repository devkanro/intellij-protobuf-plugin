---
tags: [design, annotation, quick-fix, import]
related: [design/annotation-layers]
refs: [docs/design/annotation-system.md]
---
# Annotation Quick Fixes

Annotators don't just report problems — they attach actionable repairs via `withFix()`. Annotations without fixes are complaints; annotations with fixes are workflows.

## AddImportFix

When `ProtobufAnnotator` encounters an unresolved type name, it creates an `ERROR` annotation with an `AddImportFix`. The fix:
1. Searches the stub index by short name
2. Filters candidates by qualified name suffix
3. If one match — inserts the import directly
4. If multiple — presents a popup

Implements `HintAction` so the fix appears as an inline suggestion without requiring Alt+Enter.

## RenameFix

Naming convention violations attach a `RenameFix` that delegates to `RefactoringFactory.createRename()`. This ensures all references update atomically — critical because protobuf names propagate into generated code across multiple languages.

## OptimizeImportsFix

Unused import warnings (from `FileTracker`) attach an `OptimizeImportsFix` that removes all unused imports in one action, matching the "Optimize Imports" idiom from Java/Kotlin.

## Design Principle

By co-locating detection and repair, the code that best understands the problem also provides the solution — no indirection through a separate fix-registration system.
