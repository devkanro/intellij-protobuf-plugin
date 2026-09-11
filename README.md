<div align="center">

# ![Logo](resources/logo.svg) IntelliJ Protobuf Language Plugin

**Full-featured Protocol Buffers support for JetBrains IDEs**

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/16422?label=Marketplace)](https://plugins.jetbrains.com/plugin/16422-protobuf)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/16422?label=Downloads)](https://plugins.jetbrains.com/plugin/16422-protobuf)
[![Build](https://github.com/devkanro/intellij-protobuf-plugin/workflows/Build/badge.svg)](https://github.com/devkanro/intellij-protobuf-plugin/actions)

*Semantic analysis · Cross-file navigation · Multi-language integration · Protobuf Editions*

</div>

---

<!-- Plugin description -->

## What It Does

This plugin turns your JetBrains IDE into a protobuf-aware development environment — not just syntax highlighting, but deep understanding of your schema.

> [!WARNING]
> Not compatible with the bundled [JetBrains Protocol Buffer plugin](https://plugins.jetbrains.com/plugin/14004-protocol-buffers).
> Disable **Protocol Buffer** and **gRPC** before installing.

<!-- Plugin description end -->

## 🚀 Quick Start

1. Open **Settings → Plugins → Marketplace**
2. Search for **"Protobuf"** by kanro
3. Install and restart

### Upgrading from the old language IDs

The language IDs are now `protocol_buffers` (previously `protobuf`) and
`protocol_buffers_text` (previously `prototext`). File type names, extensions, display names,
plugin settings, and color scheme keys are unchanged. No aliases for the old language IDs are registered.

Language-specific code style settings and custom integrations that reference the old IDs may need
manual migration. Back up your IDE and project settings first. For code style schemes, copy only
the `codeStyleSettings` entry for the old language to the new language ID **if the destination is
absent**; keep the original entry and never overwrite existing settings for the new ID.
The separate `ProtobufCodeStyleSettings` entry does not need renaming.
Update custom language injection targets, `@Language` annotations, and language injection comments
that reference the old IDs only when they are intended for this plugin, not the bundled plugin.
Live Template context IDs are separate from language IDs and are not renamed; review custom template
applicability rather than replacing their context IDs. Third-party plugins must update their language
bindings or lookups to the new IDs.

Migration is not automatic: the old IDs can also identify JetBrains' bundled languages, so their
settings cannot safely be attributed to this plugin. Legacy code style entries are retained by the
platform, but are not automatically applied to the new languages. Ordinary file associations retain their
existing file type names. This change does **not** establish full coexistence with the bundled
Protocol Buffer and gRPC plugins; the compatibility warning above still applies.

## 🔍 Feature Highlights

### Schema Intelligence

Understands your `.proto` files semantically — resolves types across files and imports, validates field numbers, checks naming conventions, and catches errors in real time.

![Screenshot](resources/screenshot.png)

### Cross-File Navigation

Go-to-definition, find usages, and rename refactoring that works across files, packages, and even library proto definitions.

<table>
<tr>
<td width="50%">

**Reference resolution**
![Reference](resources/reference.webp)

</td>
<td width="50%">

**In-place rename**
![Rename](resources/rename.webp)

</td>
</tr>
</table>

### Smart Editing

Auto-completion that understands context — types, field names, enum values, options. Auto-import and import optimization built in.

<table>
<tr>
<td width="50%">

**Auto import**
![Auto Import](resources/auto_import.webp)

</td>
<td width="50%">

**Import optimizer**
![Import Optimizer](resources/import_optimizer.webp)

</td>
</tr>
</table>

### Multi-Language Integration

| Module | Features |
|--------|----------|
| **Java / Kotlin** | Navigate between `.proto` and generated Java code |
| **Go** | Go code navigation, decompile from proto descriptors |
| **gRPC** | Send requests via [HTTP Client](https://plugins.jetbrains.com/plugin/13121-http-client), explore APIs via [Endpoints](https://plugins.jetbrains.com/plugin/16890-endpoints) |
| **AIP** | [Google API design](https://google.aip.dev/) spec validation and completion |
| **Sisyphus** | [Sisyphus](https://github.com/ButterCam/sisyphus) Kotlin framework integration |

### Modern Protobuf

- [Protobuf Editions](https://protobuf.dev/editions/overview/) (`edition = "2023"`) support
- [Text Format](https://protobuf.dev/reference/protobuf/textformat-spec/) (`.textproto`) with schema-based validation
- [Buf](https://buf.build/) support via [companion plugin](https://plugins.jetbrains.com/plugin/19147-buf-for-protocol-buffers)

### AIP Spec Support

Validates and auto-completes [Google API Improvement Proposals](https://google.aip.dev/) — resource definitions, HTTP rules, and naming conventions.

![AIP](resources/aip.webp)

## 🤖 Copilot-Powered Development

This project uses AI-assisted development with a structured workflow:

```
brainstorm → implement → ship → reflect
```

Every non-trivial change goes through a **brainstorm** (design discussion with a state machine), followed by implementation, shipping with quality gates, and post-ship reflection. Learnings accumulate in a knowledge base that feeds back into future work.

See [`.github/copilot-instructions.md`](.github/copilot-instructions.md) for the workflow and [`.github/skills/`](.github/skills/) for the skill definitions.

## 📖 Documentation

| | |
|---|---|
| **[Overview](docs/overview.md)** | What the plugin does and why |
| **[Architecture](docs/architecture.md)** | How the pieces fit together |
| **[Getting Started](docs/getting-started.md)** | Build, run, and test |
| **[Contributing](docs/contributing.md)** | Code style and how to add features |
| **[Design Docs](docs/design/)** | Deep-dive into subsystem design decisions |
| **[Extension Points](docs/extension-points.md)** | API for extending the plugin |

## 🙏 Acknowledgments

Inspired by [protobuf-jetbrains-plugin](https://github.com/ksprojects/protobuf-jetbrains-plugin) and [intellij-protobuf-editor](https://github.com/jvolkman/intellij-protobuf-editor).