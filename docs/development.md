# Development Guide

## Prerequisites

- **JDK 17** — Required for building and running
- **IntelliJ IDEA** — Ultimate Edition recommended (for full plugin development support)
- **Gradle 8.7** — Bundled via wrapper (`gradlew`)

## Building

```bash
./gradlew build
```

This will:
1. Generate parser and lexer from grammar files (`src/main/grammar/`)
2. Compile Kotlin/Java sources
3. Run tests
4. Build the plugin artifact

## Running

To run the plugin in a sandboxed IDE instance:

```bash
./gradlew runIde
```

## Testing

```bash
./gradlew test
```

Test sources are in `src/test/`.

## Project Structure

```
+-- build.gradle.kts          # Build configuration & dependencies
+-- gradle.properties          # Plugin version, platform version
+-- settings.gradle.kts        # Project name
+-- src/
|   +-- main/
|   |   +-- grammar/           # BNF & FLEX grammar files
|   |   +-- java/              # Java sources (parser util)
|   |   +-- kotlin/            # Kotlin sources (main codebase)
|   |   +-- resources/         # Plugin descriptors, icons, built-in protos
|   +-- test/                  # Test sources
+-- resources/                 # Marketing assets (screenshots, logo)
+-- docs/                      # This documentation
```

## Grammar Development

The parser and lexer are generated from grammar files:

| File | Tool | Output |
|------|------|--------|
| `protobuf.bnf` | Grammar-Kit | Parser + PSI classes |
| `protobuf.flex` | JFlex | Lexer |
| `prototext.bnf` | Grammar-Kit | Proto text parser + PSI |

Generated code goes to `build/generated/sources/grammar/`. Do not edit generated files directly — modify the grammar definitions instead.

To regenerate after grammar changes, run the build. IntelliJ also supports generating from within the IDE using Grammar-Kit plugin actions.

## Adding a New Language Feature

1. **Annotator/Inspection**: Add to `lang/annotator/`, register in `plugin.xml`
2. **Completion**: Add provider in `lang/completion/`, register in `ProtobufCompletionContributor`
3. **Quick fix**: Add to `lang/quickfix/`, register via annotator or inspection
4. **Reference**: Add provider in `lang/reference/`, register in `ProtobufSymbolReferenceContributor`

## Adding a New Integration Module

1. Create package under `src/main/kotlin/.../protobuf/<module>/`
2. Create XML config file at `src/main/resources/META-INF/io.kanro.idea.plugin.protobuf-<module>.xml`
3. Add optional dependency in main `plugin.xml`
4. Implement required providers (index, line marker, find usage)
5. Add documentation in `docs/modules/<module>.md`

## Key Configuration

### gradle.properties

```properties
pluginVersion=2.0.0
platformType=IU
platformVersion=2024.1
```

### Plugin Compatibility

The plugin supports IntelliJ platform builds `241` through `242.*` (defined in `build.gradle.kts`).

## CI/CD

- **Build workflow** (`.github/workflows/build.yml`) — Runs on push to `main` and all PRs. Builds, tests, and verifies the plugin.
- **Release workflow** (`.github/workflows/release.yml`) — Triggered by GitHub Releases. Publishes to JetBrains Marketplace.

## Code Style

- Kotlin is the primary language (99%+ of source)
- Follow existing patterns in the codebase
- PSI element behavior goes in mixins, not generated classes
- Use extension functions for utility code