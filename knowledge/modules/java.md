---
tags: [module, java, navigation, find-usages]
related: [design/external-data-system, architecture/extension-points]
refs: [docs/modules/java.md]
---
# Java Integration Module

**Package**: `io.kanro.idea.plugin.protobuf.java`
**Config**: `META-INF/io.kanro.idea.plugin.protobuf-java.xml`
**Dependency**: `com.intellij.modules.java` (optional)

## Features

- **Line Markers**: Bidirectional gutter icons between `.proto` definitions and generated Java classes
- **Find Usages**: Java code using generated proto classes appears in "Find Usages" for the proto definition
- **Index**: `JavaIndexProvider` indexes with Java class names computed from `java_package`, `java_outer_classname`, `java_multiple_files`
- **Stub External Data**: `FileJavaOptionsProvider` extracts Java options at stub-creation time

## Java Name Resolution (`Names.kt`)

- Package: `java_package` option → proto package fallback
- Outer class: `java_outer_classname` option → derived from filename
- Multiple files: when `java_multiple_files = true`, top-level messages get their own classes

## Key Files

| File | Purpose |
|------|---------|
| `JavaIndexProvider.kt` | Stub index contributions |
| `JavaLineMarkerProvider.kt` | Java-side gutter icons |
| `ProtobufLineMarkerProvider.kt` | Proto-side gutter icons |
| `JavaFindUsageFactory.kt` | Find usage integration |
| `FileJavaOptionsProvider.kt` | Java option stub data |
| `Names.kt` | Java naming rules |
