---
tags: [design, completion, insert-handler]
related: [design/completion-providers]
refs: [docs/design/code-completion.md]
---
# Smart Insert Handlers

Each lookup element carries a custom `InsertHandler` that transforms the document *beyond* just inserting text — adding closing delimiters, positioning the cursor, auto-incrementing field numbers, adding imports, and triggering follow-up completion.

## Key Handler Classes

- **`SmartInsertHandler`** — Core handler that inserts text at a given offset, avoids duplicating text already present (via `commonPrefixWith`), and optionally triggers follow-up completion
- **`AddImportInsertHandler`** — Adds an import statement when completing a cross-file type
- **`AutoPopupInsertHandler`** — Triggers `autoPopupMemberLookup` to chain completions
- **`ComposedInsertHandler`** — Sequences multiple handlers for complex insertions

## Workflow, Not Word Lookup

Completing a field name isn't "insert text" — it's "insert name, assign next field number, terminate with semicolon, position cursor." Each context is a workflow.

The provider-per-context model makes workflows composable and independently testable. The smart insert handler chain means each step can be mixed and matched.

## AIP Extension

The AIP completion system (`AipCompletionContributor`) demonstrates extensibility: it adds Google API design pattern suggestions without modifying core completion logic.
