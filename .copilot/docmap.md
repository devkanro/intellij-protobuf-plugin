---
updated: 2026-03-09
---

# Documentation Map

## Files

| File | Purpose | Related Code |
|---|---|---|
| README.md | Project overview, features, screenshots | — |
| CHANGELOG.md | Version history, release notes | All changes |
| CODE_OF_CONDUCT.md | Community guidelines | — |
| docs/architecture.md | Plugin architecture, layers, patterns | All `src/main/kotlin/` |
| docs/development.md | Build, test, contribute guide | build.gradle.kts, gradle.properties, .github/workflows/ |
| docs/extension-points.md | Custom extension point API docs | lang/root/, lang/psi/feature/, compile/ |
| docs/modules/java.md | Java integration module | java/ |
| docs/modules/go.md | Go integration module | golang/ |
| docs/modules/grpc.md | gRPC and microservices modules | grpc/, microservices/ |
| docs/modules/aip.md | AIP spec support module | aip/ |
| docs/modules/sisyphus.md | Sisyphus framework integration | sisyphus/ |

## Sections Index

### README.md
- Features overview -> all modules
- Installation -> JetBrains Marketplace
- Screenshots -> resources/

### CHANGELOG.md
- [Unreleased] -> current branch changes
- [x.y.z] -> tagged releases

### docs/architecture.md
- Parsing & Lexing -> src/main/grammar/, lang/ProtobufLanguage.kt
- PSI Layer -> lang/psi/
- Indexing & Stub -> lang/psi/stub/, lang/root/
- Language Support -> lang/completion/, lang/annotator/, lang/formatter/, lang/reference/
- UI & Settings -> lang/settings/, lang/ui/, lang/actions/
- Integration Modules -> java/, golang/, sisyphus/, grpc/, aip/
- Extension Points -> plugin.xml

### docs/development.md
- Building -> build.gradle.kts
- Grammar Development -> src/main/grammar/
- Adding Features -> lang/ subpackages
- Adding Modules -> META-INF/, plugin.xml
- CI/CD -> .github/workflows/

### docs/extension-points.md
- rootProvider -> lang/root/ProtobufRootProvider
- symbolReferenceProvider -> lang/psi/feature/ProtobufSymbolReferenceProvider
- indexProvider -> lang/psi/feature/ProtobufIndexProvider
- stubExternalProvider -> lang/psi/feature/ProtobufStubExternalProvider
- protocPlugin -> compile/ProtobufCompilerPlugin