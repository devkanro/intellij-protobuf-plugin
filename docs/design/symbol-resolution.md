# Symbol Resolution

## The Problem

Resolving symbols in protobuf is harder than it looks. A bare name like `Address`
could refer to a sibling message, a nested type in a parent scope, or a type
imported (possibly transitively) from another file. A dotted name like
`google.protobuf.Timestamp` might be an absolute path or a relative one rooted in
the current package. The language spec demands a specific resolution order — walk
up the enclosing scope chain first, then search imports — and introduces `public
import` for transitive re-export, adding a graph traversal on top of the tree
walk.

Meanwhile, the *set of files* that participate in resolution depends on the
project layout: Gradle puts protos in `src/main/proto/`, Bazel computes roots
from BUILD files, and users may configure arbitrary paths. An IDE plugin must
handle all of these without hard-coding any of them.

The challenge, then, is three-fold:

1. **Scope nesting** — names are relative to an arbitrarily deep message/enum
   hierarchy, and resolution must walk outward.
2. **Import graph** — each file imports others, public imports are transitive,
   and the resolver must traverse this graph without re-entering visited nodes.
3. **File discovery** — the universe of proto files is project-dependent and must
   be pluggable.

## Design Decisions

### Decision 1: Absolute vs Relative as Separate Code Paths

Protobuf distinguishes two forms of type reference: a leading dot
(`.google.protobuf.Timestamp`) forces absolute lookup from the root, while an
unqualified or partially-qualified name (`Timestamp`, `inner.Msg`) triggers
relative resolution. These two paths have fundamentally different algorithms:

- **Absolute** — match the fully-qualified name against every file's package +
  definition tree. No scope walking needed.
- **Relative** — walk up the enclosing scope chain first (`resolveInCurrent`),
  then compose the symbol with the file's package and search imports.

Splitting them avoids conditional branching inside a single hot path and makes
each algorithm's invariants easier to reason about. The decision point is trivial:
the grammar parser records whether the `TypeName` node starts with a dot token,
and the reference class calls the corresponding resolver method.

### Decision 2: Scope Hierarchy via Composition Interfaces

Scopes are modeled with two composable interfaces — `ProtobufScope` (a container
that has a qualified name and holds children) and `ProtobufScopeItem` (anything
that lives inside a scope). A `ProtobufMessageDefinition` implements *both*: it
is a scope item belonging to its parent and a scope containing its own fields and
nested types.

This dual-role design mirrors the protobuf language model directly. A message's
`qualifiedName()` is always `parent.scope() + name`, computed recursively up the
tree. Resolution walks *down* by iterating a scope's items and recursing into
nested scopes when the first component matches, while `resolveInCurrent` walks
*up* by hopping from scope to parent scope.

The key subtlety is **virtual scopes** (`ProtobufVirtualScope`). Constructs like
`oneof` and `extend` look like scopes in the grammar but *don't* create new
naming scopes in the protobuf language — a oneof's fields belong to the enclosing
message. Virtual scopes are marked as transparent: the `items()` iteration helper
unpacks their children directly into the parent scope's item list. This prevents
oneof and extend from accidentally hiding fields during resolution while still
preserving the syntactic structure for other purposes like formatting.

### Decision 3: Relative Resolution by Common-Prefix Composition

When resolving a relative name across files, the resolver must answer: "does the
symbol `Foo.Bar` in a file with package `com.example.v1` match anything in an
imported file with package `com.example.v2`?"

Rather than flattening all definitions into a global index, the resolver computes
the longest common prefix between the current file's package and the target file's
package, then appends the symbol to that prefix. If the resulting qualified name
falls within the target file's package namespace, the resolver descends into the
target file's definition tree for the remaining components.

This approach is correct because protobuf's relative resolution semantics treat
package components as scope boundaries. It is also efficient: files in unrelated
packages are quickly rejected by a prefix mismatch, and no global index rebuild is
needed when a single file changes.

### Decision 4: Import Graph Traversal with Public-Import Transitivity

The resolver traverses imports as a depth-first graph walk using an explicit
stack. The critical distinction is between regular and public imports:

- **Regular imports** — symbols are available only to the importing file. These
  files are pushed onto the stack once (from the originating file) but their own
  imports are *not* followed.
- **Public imports** — symbols are re-exported. When traversing an imported file,
  the resolver pushes its `public` imports onto the stack for further traversal.

This single `if (it.public())` guard implements the full transitivity rule from
the protobuf spec. Weak imports are recognized by the parser but treated
identically to regular imports during resolution — they exist primarily for
build-system hinting, not for scoping semantics.

### Decision 5: Pluggable Root Providers

The universe of resolvable proto files is not fixed. Different build systems,
module configurations, and user preferences all affect which directories contain
proto files. The root provider system solves this with an `ExtensionPoint`:

- `ProtobufRootProvider` returns a list of `ProtobufRoot`(name + VirtualFile
  directory) for a given PSI context.
- Concrete providers cover module sources, dependent modules, classpath
  libraries, user-configured paths, and built-in well-known types.
- `ProtobufRootResolver` collects roots from all registered providers, then
  deduplicates: providers sharing the same `id()` are collapsed (only the first
  wins), and individual roots sharing the same `name` are collapsed. This lets a
  project-specific provider override a generic one by reusing its id.

Import statements like `import "google/protobuf/timestamp.proto"` are resolved by
searching for the relative path against every collected root. This is intentionally
simple — the complexity lives in *collecting* the right roots, not in the file
lookup itself.

Root computation is expensive (scanning module dependencies, JAR contents), so
providers extend `CachedProtobufRootProvider` which caches results per-file using
IntelliJ's `CachedValuesManager` with a build-system-aware modification tracker.

### Decision 6: Context-Aware Symbol Filters

Not every symbol is valid in every position. A field type must be a message or
enum, an `extend` target must be a message, and an extension field reference must
belong to a specific target message. Rather than building context awareness into
the resolver itself, resolution accepts a `PsiElementFilter` that rejects invalid
matches.

This keeps the resolver generic and reusable while pushing validation to the call
site — each reference type (`ProtobufTypeNameReference`, `ProtobufFieldReference`,
`ProtobufExtensionFieldReference`) supplies its own filter. The same resolution
code serves type lookups, field lookups, and code completion with different
filters.

## How It Fits Together

A user types `Foo.Bar` as a field type. The reference system constructs a
`ProtobufTypeNameReference` and calls `resolveRelatively` (no leading dot). The
resolver:

1. Walks up the enclosing scope chain (`resolveInCurrent`), checking each scope
   for a child `Foo` that contains `Bar`.
2. If not found locally, tries the current file at package level by composing the
   relative name with the file's package.
3. Pushes all imported files onto a stack and repeats the relative composition for
   each, following public imports transitively.
4. At each step, the `fieldType` filter rejects matches that aren't messages or
   enums.

The imported files themselves were discovered by `ProtobufRootResolver`, which
asked every registered `ProtobufRootProvider` for roots, deduplicated them, and
resolved each import path against the resulting set of directories.

## Key Insight

The design separates three independent concerns that are easy to conflate:

- **Where to look** (root providers) — project-specific, cached, extensible.
- **What order to look** (resolver algorithm) — spec-driven, deterministic: local
  scopes first, then imports with public transitivity.
- **What counts as a match** (filters) — context-specific, injected by the
  reference that initiates resolution.

Each concern changes for different reasons and at different rates. Root providers
change when build system support is added. The resolution algorithm changes only if
the protobuf spec changes. Filters change when new reference contexts are
introduced. Keeping them separate means each can evolve without disturbing the
others.
