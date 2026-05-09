---
tags: [module, aip, google-api, resource]
related: [design/completion-providers, architecture/extension-points]
refs: [docs/modules/aip.md]
---
# AIP Module

**Package**: `io.kanro.idea.plugin.protobuf.aip`
**Config**: Registered in main `plugin.xml`

Implements support for [Google API Improvement Proposals (AIP)](https://google.aip.dev/) — design guidelines for Google APIs covering resource types, standard methods, and field behaviors.

## Features

- **Annotations**: `AipAnnotator` validates resource definitions, standard method patterns (Create, Get, List, Update, Delete), and field behaviors
- **Completion**: Resource type names, AIP method pattern suggestions, standard field names
- **Resource References**: Resolves `(google.api.resource_reference)` type strings to target resource definitions, enabling cross-file navigation
- **Quick Fixes**: `AddResourceImportFix` auto-imports missing resource type definitions

## Key Files

| File | Purpose |
|------|---------|
| `AipOptions.kt` | AIP option definitions and constants |
| `annotator/AipAnnotator.kt` | AIP convention validation |
| `completion/AipCompletionContributor.kt` | AIP-aware completion |
| `method/AipSpecMethod.kt` | Standard method patterns |
| `quickfix/AddResourceImportFix.kt` | Auto-import fix |
| `reference/AipResourceReference.kt` | Resource reference resolution |
