# Contributing

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

For the design rationale behind each subsystem, see the [design documents](design/).

## Adding a New Integration Module

1. Create package under `src/main/kotlin/.../protobuf/<module>/`
2. Create XML config file at `src/main/resources/META-INF/io.kanro.idea.plugin.protobuf-<module>.xml`
3. Add optional dependency in main `plugin.xml`
4. Implement required providers (index, line marker, find usage)
5. Add documentation in `docs/modules/<module>.md`

See [Extension Points](extension-points.md) for the available extension APIs.

## Copilot Workflow

This project uses AI-assisted development with GitHub Copilot. The workflow follows a structured cycle:

```
brainstorm → implement → ship → reflect
```

- **Brainstorm**: Design discussions happen in `.github/brainstorm.md` with a state machine that prevents premature coding
- **Ship**: Commits go through build verification, test execution, and brainstorm status checks
- **Reflect**: Post-ship reflections capture learnings in `.github/knowledge.md`

See [`.github/copilot-instructions.md`](../.github/copilot-instructions.md) for the full workflow specification.
