---
updated: 2026-03-09
---

# Documentation Map

## Files

| File | Purpose | Related Code |
|---|---|---|
| README.md | Project overview, features, install, Copilot workflow | — |
| CHANGELOG.md | Version history, release notes | All changes |
| docs/README.md | Documentation index / navigation page | — |
| docs/overview.md | What the plugin does, key concepts | — |
| docs/architecture.md | High-level layer diagram, subsystem overview | All `src/main/kotlin/` |
| docs/getting-started.md | Build, run, test guide | build.gradle.kts, gradle.properties, .github/workflows/ |
| docs/contributing.md | Code style, adding features/modules, Copilot workflow | lang/, META-INF/ |
| docs/extension-points.md | Custom extension point API docs | lang/root/, lang/psi/feature/, compile/ |
| docs/design/psi-and-mixin.md | Why mixin pattern for PSI elements | lang/psi/, lang/psi/proto/mixin/, lang/psi/feature/ |
| docs/design/symbol-resolution.md | Why two-phase resolve, scope hierarchy | lang/psi/proto/ProtobufSymbolResolver, lang/psi/proto/ProtobufScope, lang/root/ |
| docs/design/stub-indexing.md | Why stubs, index design tradeoffs | lang/psi/stub/, lang/psi/stub/type/, lang/psi/stub/index/ |
| docs/design/annotation-system.md | Why per-version annotators, validation philosophy | lang/annotator/, lang/quickfix/ |
| docs/design/code-completion.md | Why provider-per-context, insert handlers | lang/completion/ |
| docs/design/compiler-system.md | Why in-process compiler, plugin architecture | compile/ |
| docs/design/prototext.md | Why separate language for text format | lang/psi/text/, prototext.bnf |
| docs/design/editions.md | Why feature-set model for editions | lang/psi/feature/ProtobufFeature, lang/annotator/ProtobufEditionAnnotator |
| docs/modules/java.md | Java integration module | java/ |
| docs/modules/go.md | Go integration module | golang/ |
| docs/modules/grpc.md | gRPC and microservices modules | grpc/, microservices/ |
| docs/modules/aip.md | AIP spec support module | aip/ |
| docs/modules/sisyphus.md | Sisyphus framework integration | sisyphus/ |