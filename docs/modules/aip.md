# AIP Support

**Package**: `io.kanro.idea.plugin.protobuf.aip`
**Config**: Registered in main `plugin.xml`

## Overview

Implements support for [Google API Improvement Proposals (AIP)](https://google.aip.dev/) — a set of design guidelines for Google APIs. The AIP spec defines conventions for resource types, standard methods, and field behaviors that this module validates and assists with.

## Features

### Annotations

`AipAnnotator` validates AIP conventions in proto files:
- Resource definition correctness
- Standard method patterns (Create, Get, List, Update, Delete)
- Field behavior annotations

### Completion

`AipCompletionContributor` provides:
- Resource type name completion
- AIP method pattern suggestions
- Standard field names for AIP resources

### Resource References

Resolves `(google.api.resource_reference)` type strings to their target resource definitions. This allows navigation from a resource reference in one proto file to the resource definition in another.

### Quick Fixes

- `AddResourceImportFix` — Auto-import missing resource type definitions

### Spec Methods

`AipSpecMethod` defines the standard AIP method patterns and their expected signatures.

## Key Files

| File | Purpose |
|------|---------|
| `AipOptions.kt` | AIP option definitions and constants |
| `Extension.kt` | Extension declarations |
| `annotator/AipAnnotator.kt` | AIP convention validation |
| `completion/AipCompletionContributor.kt` | AIP-aware completion |
| `method/AipSpecMethod.kt` | Standard method patterns |
| `quickfix/AddResourceImportFix.kt` | Auto-import fix |
| `reference/AipResourceReference.kt` | Resource reference resolution |
| `reference/AipResourceResolver.kt` | Resource type resolver |