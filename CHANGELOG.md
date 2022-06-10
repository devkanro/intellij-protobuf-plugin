<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# IntelliJ Protobuf Language Plugin Changelog

## [Unreleased]

## [1.6.10]
### Fixed
- Fix plugin setting page on non-IDEA IDEs

## [1.6.0]
### Added
- Support send gRPC request via HTTP client
  plugin [#102](https://github.com/devkanro/intellij-protobuf-plugin/issues/102)
- Use the common folding setting to config import
  folding [#100](https://github.com/devkanro/intellij-protobuf-plugin/issues/100)
- Inject Markdown to Protobuf line comment blocks
- Support disable decompile from go descriptor [#99](https://github.com/devkanro/intellij-protobuf-plugin/issues/99)

## [1.5.12]
### Fixed
- Fix type suggestions by stub indexing.

## [1.5.11]
### Fixed
- Fix buf toolwindow tree cell rendering in IU-2021.3.3

## [1.5.10]
### Fixed
- Fix protobuf settings not being saved.

## [1.5.0]
### Added
- Basically code completion and documentation for buf configuration files.
- Auto config protobuf import roots from buf.yaml and buf.work.yaml, it support locked deps and workspace local module.
- Pre-provided well-known protos.
- Buf lint annotator.
- Run buf command configuration.
- Buf tool window support.

## [1.4.8]
### Added
- Add more rule for code formatting
- Add arrange field numbers actions

## [1.4.7]
### Fixed
- Fix sub message resolving with cross file

## [1.4.6]
### Added
- Support repeated string value for constant

## [1.4.5]
### Fixed
- Fix recursive importing for auto import

### Changed
- Target to intellij 2021.3

## [1.4.4]
### Added
- Support mark root path as non-common root

## [1.4.3]
### Added
- Support multi field name in options #42
- Support `returns` keyword auto-completion #47
- Add more `extend` checks

## [1.4.2]
### Added
- gRPC endpoints support
- Decompile proto source from generated go code
- Resolve proto in go src root
- Resolve proto in decompiled protos
- Implementing navigation for golang

### Fixed
- Fix rename proto file exception

## [1.4.1]
### Added
- Java navigation and find usage support
- Quick fix for resource name

### Fixed
- Fix rename quick fix

## [1.4.0]
### Added
- Sisyphus navigation support

### Changed
- Support array in protobuf options
- Upgrade to IntelliJ platform 212 EAP

## [1.3.1]
### Added
- Auto suggest field number when pick suggested field name

## [1.3.0]
### Added
- Add line breaking settings for protobuf code style
- Resolve symbol reference in quick document
- Field name and enum value name suggestion

## [1.2.1]
### Fixed
- Fix boundary problem to resolve context range in string value
- Fix highlight for keyword in package name

## [1.2.0]
### Added
- Add quick fix for unimported symbols in string
- Colorful symbols

### Fixed
- Fix hex int parsing in field definition (#12)

## [1.1.0]
### Added
- Add quick fix for unimported symbols
- Auto sort imports

## [1.0.0]
### Added
- Add quick document with commonmark
- Add 'json_name' option support
- Add 'allow_alias' option support

### Fixed
- 'default' option value type annotation

## [0.0.12]
### Added
- Add case annotator for definitions

## [0.0.11]
### Added
- Add support for platform api 201

## [0.0.10]
- Add [Long running operation](https://aip.bybutter.com/151) support for AIP-151

## [0.0.9]
### Added
- Add dep module source root file resolver
- Add resource type auto import

## [0.0.8]
### Added
- Add PSI stub support
- Add goto symbol contributor
- Add auto import for symbols

## [0.0.7]
### Added
- Add icons for stream method
- Add method auto-completion
- Add cache for collect proto in archive

### Changed
- Change logo

## [0.0.6]
### Added
- Add [Resource Type](https://aip.bybutter.com/123) support for AIP-123
- Add [Http transcoding](https://aip.bybutter.com/127) support for AIP-127
- Add spellchecker
- Add in-place rename support

## [0.0.5]
### Changed
- Add enum value highlight in option usage
- Fix import optimizer action
- Optimize import auto completion

## [0.0.4]
### Added
- Duplicate and unused import annotator
- Import optimizer
- Auto-completion for import
- Custom import root

### Changed
- Fix some keywords highlight in typename(etc. 'rpc')
- Refactor psi structure by default implementation
- New reference and resolving cache implementation

## [0.0.3]
### Added
- Basically features for protobuf language