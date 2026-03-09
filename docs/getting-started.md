# Getting Started

## Prerequisites

- **JDK 21** — Required for building and running
- **IntelliJ IDEA** — Ultimate Edition recommended (for full plugin development support)
- **Gradle 8.10.2** — Bundled via wrapper (`gradlew`)

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
├── build.gradle.kts          # Build configuration & dependencies
├── gradle.properties          # Plugin version, platform version
├── settings.gradle.kts        # Project name
├── src/
│   ├── main/
│   │   ├── grammar/           # BNF & FLEX grammar files
│   │   ├── java/              # Java sources (parser util)
│   │   ├── kotlin/            # Kotlin sources (main codebase)
│   │   └── resources/         # Plugin descriptors, icons, built-in protos
│   └── test/                  # Test sources
├── resources/                 # Marketing assets (screenshots, logo)
└── docs/                      # This documentation
```

## Key Configuration

### gradle.properties

```properties
pluginVersion=2.3.0
platformType=IU
platformVersion=2025.3
```

### Plugin Compatibility

The plugin supports IntelliJ platform builds `253` through `253.*` (defined in `build.gradle.kts`).

## Grammar Development

The parser and lexer are generated from grammar files:

| File | Tool | Output |
|------|------|--------|
| `protobuf.bnf` | Grammar-Kit | Parser + PSI classes |
| `protobuf.flex` | JFlex | Lexer |
| `prototext.bnf` | Grammar-Kit | Proto text parser + PSI |
| `prototext.flex` | JFlex | Proto text lexer |

Generated code goes to `build/generated/sources/grammar/`. Do not edit generated files directly — modify the grammar definitions instead.

To regenerate after grammar changes, run the build. IntelliJ also supports generating from within the IDE using Grammar-Kit plugin actions.

## CI/CD

- **Build workflow** (`.github/workflows/build.yml`) — Runs on push to `main` and all PRs
- **Release workflow** (`.github/workflows/release.yml`) — Triggered by GitHub Releases, publishes to JetBrains Marketplace
