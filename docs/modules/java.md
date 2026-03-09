# Java Integration

**Package**: `io.kanro.idea.plugin.protobuf.java`
**Config**: `META-INF/io.kanro.idea.plugin.protobuf-java.xml`
**Dependency**: `com.intellij.modules.java` (optional)

## Overview

Provides bidirectional navigation between `.proto` definitions and their generated Java code. When Java support is available in the IDE, gutter icons appear in both proto and Java files allowing one-click navigation.

## Features

### Line Markers

- **Proto -> Java**: Gutter icon on message/service/enum definitions linking to generated Java classes
- **Java -> Proto**: Gutter icon on generated Java classes linking back to the proto source

### Find Usages

Java code that uses generated proto classes will appear in "Find Usages" results for the corresponding proto definition.

### Index

The `JavaIndexProvider` indexes proto elements with their Java class names, computed from:
- `java_package` option (or proto package if unset)
- `java_outer_classname` option
- `java_multiple_files` option

### Name Resolution

`Names.kt` implements the Java naming rules:
- Package: `java_package` option -> proto package
- Outer class: `java_outer_classname` option -> derived from filename
- Multiple files: when `java_multiple_files = true`, top-level messages get their own classes

## Key Files

| File | Purpose |
|------|---------|
| `Extension.kt` | Extension point declarations |
| `JavaIndexProvider.kt` | Stub index contributions |
| `JavaLineMarkerProvider.kt` | Java-side gutter icons |
| `ProtobufLineMarkerProvider.kt` | Proto-side gutter icons |
| `JavaFindUsageFactory.kt` | Find usage integration |
| `JavaNameIndex.kt` | Name index |
| `FileJavaOptionsProvider.kt` | Java option stub data |
| `Names.kt` | Java naming rules |