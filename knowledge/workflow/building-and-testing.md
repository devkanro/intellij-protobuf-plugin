---
tags: [workflow, build, test, setup]
related: [workflow/contributing-guide]
refs: [docs/getting-started.md]
---
# Building and Testing

## Prerequisites

- **JDK 21** — Required for building and running
- **IntelliJ IDEA** — Ultimate Edition recommended
- **Gradle 8.10.2** — Bundled via wrapper (`gradlew`)

## Commands

```bash
./gradlew build    # Generate parser/lexer, compile, test, build artifact
./gradlew runIde   # Run plugin in sandboxed IDE instance
./gradlew test     # Run tests only
```

## Project Structure

```
├── build.gradle.kts          # Build configuration & dependencies
├── gradle.properties          # Plugin version, platform version
├── src/
│   ├── main/
│   │   ├── grammar/           # BNF & FLEX grammar files
│   │   ├── java/              # Java sources (parser util)
│   │   ├── kotlin/            # Kotlin sources (main codebase)
│   │   └── resources/         # Plugin descriptors, icons, built-in protos
│   └── test/                  # Test sources
├── resources/                 # Marketing assets (screenshots, logo)
└── docs/                      # Documentation
```

## Grammar Development

| File | Tool | Output |
|------|------|--------|
| `protobuf.bnf` | Grammar-Kit | Parser + PSI classes |
| `protobuf.flex` | JFlex | Lexer |
| `prototext.bnf` | Grammar-Kit | Proto text parser + PSI |
| `prototext.flex` | JFlex | Proto text lexer |

Generated code goes to `build/generated/sources/grammar/`. Never edit generated files — modify grammar definitions instead.

## CI/CD

- **Build workflow** (`.github/workflows/build.yml`) — Runs on push to `main` and all PRs
- **Release workflow** (`.github/workflows/release.yml`) — Triggered by GitHub Releases, publishes to JetBrains Marketplace
