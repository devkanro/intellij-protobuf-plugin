# Stub Indexing

## The Problem

A protobuf project of any real size has hundreds of `.proto` files with deep import chains.
Google's well-known types alone are ~30 files; a large API surface (googleapis, gRPC
definitions) easily reaches thousands. Every IDE feature — go-to-symbol, completion,
find-usages, reference resolution — needs to answer the question "what symbols exist across
all these files?"

The naïve answer is: parse every file, walk every AST. But parsing is expensive. A full
parse builds a complete syntax tree — comments, whitespace, option bodies, field numbers —
most of which is irrelevant when you just need to know "what's the qualified name of every
message and service?" Doing this on every keystroke would freeze the IDE.

IntelliJ's stub system solves this by letting us **pre-extract the identity of each symbol
and serialize it to disk**. On subsequent IDE starts, the platform reads lightweight binary
stubs instead of re-parsing source files. Stubs are the foundation of all index-based
lookups — they make it possible to resolve `google.protobuf.Timestamp` without parsing
`timestamp.proto` on every reference.

Protobuf makes this especially critical because:
- **Deep import graphs**: a single file can transitively import hundreds of others via
  `import` and `import public`.
- **Cross-language code generation**: Java, Go, and Kotlin each derive different generated
  names from the same `.proto` file, so the index must support multiple naming schemes
  simultaneously.
- **Large well-known type sets**: standard libraries (`google/protobuf/`, `google/api/`)
  are always in scope and would be re-parsed constantly without caching.

## Design Decisions

### Decision 1: What Gets Stubbed (and What Doesn't)

**Stubbed** (11 element types): `Message`, `Enum`, `EnumValue`, `Service`, `Rpc`, `Field`,
`MapField`, `Oneof`, `Group`, `Extend`, `PackageName`.

**Not stubbed**: field types, field numbers, option values, comments, default values,
method request/response types.

**Why this boundary?** The stubbed elements are exactly the set that defines the
**namespace tree** — the hierarchy of named scopes a user navigates when typing
`package.Message.NestedEnum.VALUE`. Stubs capture *identity* (what things are named) and
*structure* (what nests inside what). They deliberately skip *content* (what types fields
reference, what values options hold) because content requires **cross-file resolution**,
which is the very thing stubs exist to avoid.

The tradeoff is concrete: each stub stores a small `Array<String>` (typically 1-2 entries:
name, and optionally a resource type annotation). Adding field type information would mean
storing qualified type references that might themselves require resolution to be useful —
defeating the purpose of the lightweight index. By keeping stubs identity-only, a single
binary read gives us the full symbol table for a file.

### Decision 2: Three Core Index Types

The plugin registers three `StringStubIndexExtension`s, each optimized for a different
access pattern:

| Index | Key | Optimizes |
|-------|-----|-----------|
| **ShortNameIndex** | Simple name (`"Timestamp"`) | Completion — "user typed `Time`, show all matches" |
| **QualifiedNameIndex** | Full path (`"google.protobuf.Timestamp"`) | Reference resolution — "resolve this exact symbol" |
| **ResourceTypeIndex** | AIP resource type (`"type.googleapis.com/..."`) | AIP compliance — "find the message for this resource" |

**Why not just one?** Because the access patterns are fundamentally different:

- **Completion** needs fuzzy matching on short names across the entire project. A qualified
  name index would require the user to know the full path before they can search — exactly
  what completion is supposed to help with.
- **Resolution** needs exact lookup by qualified name. Searching a short-name index for
  `"Timestamp"` would return every message named `Timestamp` in every package, requiring a
  filtering pass that defeats O(1) lookup.
- **Resource types** are a Google AIP concept orthogonal to protobuf's own naming.
  A message's resource type (`google.api.resource` annotation) doesn't follow the package
  hierarchy, so it needs its own key space. Only `ProtobufMessageStub` populates this
  index.

All three indices are populated in `ProtobufStubTypeBase.indexStub()` during a single
serialization pass, so the cost of maintaining multiple indices is negligible.

### Decision 3: The External Data System

Each stub carries a `Map<String, String>` of **external data**, populated by
`ProtobufStubExternalProvider` implementations at stub-creation time.

The motivating problem: protobuf files contain **language-specific options** that control
code generation — `java_package`, `java_outer_classname`, `go_package`, `json_name`. These
options determine the names of generated classes in downstream languages. The plugin needs
this data to navigate from generated Java/Go/Kotlin code back to the originating `.proto`
definition, but it shouldn't be baked into the core stub format because:

1. **Not all users need all languages.** A Go shop shouldn't pay index overhead for Java
   class names.
2. **New languages appear.** Hardcoding language support into the stub schema would require
   a stub version bump (cache invalidation) for every new language module.
3. **Options are open-ended.** Custom protobuf options (e.g., `sisyphus` framework
   annotations) are project-specific.

The solution splits into two extension points:

- **`stubExternalProvider`**: Extracts option values during stub creation and stores them in
  the external data map. Example: `FileJavaOptionsProvider` reads `java_package`,
  `java_outer_classname`, and `java_multiple_files` from file options, and `json_name` from
  field options. This data is serialized into the binary stub and available without
  re-parsing.

- **`indexProvider`**: Builds custom index entries from stub data during the indexing pass.
  Example: `JavaIndexProvider` computes generated gRPC stub class names
  (`*ImplBase`, `*BlockingStub`, `*FutureStub`, `*CoroutineStub`) and indexes them under
  `JavaNameIndex`. `GoIndexProvider` does the same for Go client/server interface names.
  `ServiceMethodIndexProvider` indexes gRPC method paths (`package.Service/Method`).

This two-phase design means the core stub format (`Array<String>` + `Map<String, String>`)
is stable, while language-specific indexing logic lives in optional modules (`protobuf-java`,
`protobuf-go`, `protobuf-sisyphus`) that can evolve independently.

### Decision 4: Scope-Carrying Stubs for Qualified Name Computation

A distinctive design choice: **qualified names are computed from the stub tree itself**,
not stored verbatim.

`ProtobufDefinitionStub.qualifiedName()` walks the parent stub chain via
`parentOfType<ProtobufScopeStub>()?.scope()?.append(name)`. Each scope-defining stub
(`ProtobufFileStubImpl`, `ProtobufMessageStub`, `ProtobufEnumStub`, `ProtobufServiceStub`)
implements `ProtobufScopeStub.scope()` to return its own qualified name, forming a
recursive chain: file stub provides the package prefix, message stubs append their names,
and leaf definitions (fields, enum values, RPCs) append theirs.

**Why compute instead of store?** Because storing qualified names for every nested element
would duplicate the package path hundreds of times per file. A file with package
`google.cloud.bigquery.v2` containing 50 messages with 500 fields total would store that
prefix 550 times. Computing it from the tree structure stores the package name exactly
once (in the file stub) and each element stores only its own short name.

This also means the stub tree mirrors the protobuf scope hierarchy exactly — which is why
`PackageName` is stubbed despite not being a "definition" in the usual sense. It establishes
the root scope for the entire file.

### Decision 5: Single Global Stub Version

The file stub version is a single integer (`getStubVersion() = 1`), not a composite of
per-element or per-provider versions. Changing the stub format for *any* element type
requires bumping this number, which invalidates the entire stub cache.

This is a deliberate simplicity-over-granularity tradeoff. Protobuf's schema language
evolves slowly — the set of stubbed constructs (message, enum, service, etc.) hasn't
changed in years. A per-element versioning scheme would add complexity for a migration that
almost never happens. The external data map (`Map<String, String>`) absorbs most
variability by being schema-free — new keys can be added by providers without changing the
binary format.

## How It Fits Together

```
                        Parse                    Serialize
  .proto file  ───────────────►  PSI Tree  ─────────────────►  Binary Stub
                                    │                              │
                              stubData()                    readStringArray()
                           stubExternalData()                  readMap()
                                    │                              │
                                    ▼                              ▼
                             Stub Tree ◄──────────────────── Stub Tree
                                    │         Deserialize
                                    │
                              indexStub()
                              ┌─────┼──────────────┐
                              ▼     ▼              ▼
                         ShortName  QualifiedName  ProtobufIndexProvider
                          Index      Index           ├─ JavaIndexProvider
                                       │             ├─ GoIndexProvider
                                       │             └─ ServiceMethodIndexProvider
                                       ▼
                              ResourceTypeIndex
                            (messages only)

  Query time:
    Completion        → ShortNameIndex.key      → fuzzy match  → stub PSI
    Go-to-symbol      → QualifiedNameIndex.key  → exact lookup → stub PSI
    Java navigation   → JavaNameIndex.key       → gRPC stubs   → stub PSI
    AIP resource      → ResourceTypeIndex.key   → annotation   → stub PSI
```

The dual-constructor pattern in PSI mixins (e.g., `ProtobufMessageDefinitionMixin`) makes
this transparent to consumers: the same `ProtobufMessageDefinition` interface works whether
backed by a parsed AST node or a deserialized stub. Code that calls `element.name()` or
`element.qualifiedName()` doesn't know or care which path created the element.

## Key Insight

The stub system's power comes from a strict separation: **identity is cheap, content is
expensive**. By extracting only the names and nesting structure of protobuf definitions —
and deferring everything else (types, options, bodies) to on-demand parsing — the plugin
can maintain a complete symbol table for arbitrarily large protobuf projects at effectively
zero runtime cost. The extension point system (`stubExternalProvider` + `indexProvider`)
then layers language-specific concerns on top without coupling them to the core schema,
letting Java, Go, and custom framework support evolve independently while sharing the same
underlying stub infrastructure.
