# ![Logo](resources/logo.svg)IntelliJ Protobuf Language Plugin

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/16422) ![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/16422)](https://plugins.jetbrains.com/plugin/16422-protobuf)

## Reference

Inspired by [protobuf-jetbrains-plugin](https://github.com/ksprojects/protobuf-jetbrains-plugin)
and [intellij-protobuf-editor](https://github.com/jvolkman/intellij-protobuf-editor).

## Descriptor

<!-- Plugin description -->
IntelliJ-based IDEs Protobuf Language Plugin that provides Protobuf language support.  

> ⚠️ Attention ⚠️  
> This plugin is not compatible with [Jetbrains Official Protobuf Plugin](https://plugins.jetbrains.com/plugin/14004-protocol-buffers) bundled in 2021.2 and later.  
> You should disable **Protocol Buffer** and **gRPC** to use this plugin.

Analyzing features:

✅ Syntax highlighting  
✅ Symbol and References  
✅ Import file from library and SDK  
✅ Navigation  
✅ Find Usage  
✅ Code folding  
✅ Semantic analysis  
✅ Struct Viewer  
✅ Quick documentation  
✅ PSI stub  
✅ Java/Kotlin support  
✅ [Sisyphus](https://github.com/ButterCam/sisyphus) framework integration  
✅ Decompile from proto descriptor for golang  
✅ [Buf](https://buf.build) integration, auto-configure the protobuf roots from buf.yaml and buf.work.yaml  
✅ [Buf](https://buf.build) integration, run buf command in buf tool window  
✅ [Buf](https://buf.build) integration, annotator by buf linter

Editor features:

✅ Auto Completion  
✅ Code format  
✅ Import optimizing  
✅ [AIP](https://google.aip.dev/) spec support  
✅ Auto import  
✅ Import quick fix  
✅ [Buf](https://buf.build) integration, code completion for buf configraution files

<!-- Plugin description end -->

Planned features:

🙋 Proto text support  

## Screenshots

![screenshot](resources/screenshot.png)

### Highlight Features

1. Import optimizer  
   ![import](resources/import_optimizer.gif)

2. Reference  
   ![reference](resources/reference.gif)
   
3. In-place rename  
   ![rename](resources/rename.gif)

4. Auto completion  
   ![import](resources/import.gif)

5. Auto import  
   ![auto import](resources/auto_import.gif)
   
6. AIP Spec
   ![resource](resources/aip.gif)
   
More features wait for your discovering...
