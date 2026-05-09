---
tags: [workflow, contributing, code-style]
related: [workflow/building-and-testing, design/psi-mixin-injection]
refs: [docs/contributing.md]
---
# Contributing Guide

## Code Style

- Kotlin is the primary language (99%+ of source)
- Follow existing patterns in the codebase
- PSI element behavior goes in mixins, not generated classes
- Use extension functions for utility code

## Adding a New Language Feature

1. **Annotator/Inspection**: Add to `lang/annotator/`, register in `plugin.xml`
2. **Completion**: Add provider in `lang/completion/`, register in `ProtobufCompletionContributor`
3. **Quick fix**: Add to `lang/quickfix/`, register via annotator or inspection
4. **Reference**: Add provider in `lang/reference/`, register in `ProtobufSymbolReferenceContributor`

## Adding a New Integration Module

1. Create package under `src/main/kotlin/.../protobuf/<module>/`
2. Create XML config at `src/main/resources/META-INF/io.kanro.idea.plugin.protobuf-<module>.xml`
3. Add optional dependency in main `plugin.xml`
4. Implement required providers (index, line marker, find usage)
5. Add documentation in `docs/modules/<module>.md`

## Copilot Workflow

This project uses AI-assisted development following: `brainstorm → implement → ship → reflect`. Design discussions happen in `.github/brainstorm.md`, commits go through build verification, and post-ship reflections are captured in `.github/knowledge.md`.
