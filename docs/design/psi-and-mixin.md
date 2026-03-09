# PSI & Mixin Pattern

## The Problem

An IntelliJ language plugin needs a **Program Structure Interface (PSI)** — a typed
tree of elements the IDE can query for navigation, completion, refactoring, and
indexing. For protobuf this tree is non-trivial: messages nest inside messages,
fields reference types across files, qualified names chain arbitrarily, and two
distinct file formats (`.proto` and `.textproto`) share many semantic concepts but
have different grammars.

A hand-written PSI would couple syntax to semantics. Every grammar change would
ripple through reference resolution, completion, stubs, and folding. The
codebase solves this by splitting the problem into **four independent layers**
that compose at code-generation time, so each concern can evolve on its own.

## Design Decisions

### Decision 1: BNF-Generated PSI with Mixin Injection

The Grammar-Kit BNF files (`protobuf.bnf`, `prototext.bnf`) are the **single
source of truth** for PSI structure. Every grammar rule can declare three
orthogonal extension points:

```bnf
MessageDefinition ::= message Identifier MessageBody {
    implements=[ "...ProtobufElement", "...BodyOwner", "...ProtobufOptionOwner" ]
    mixin="...ProtobufMessageDefinitionMixin"
    stubClass="...ProtobufMessageStub"
}
```

| Attribute | Role |
|-----------|------|
| `implements` | Bolt on cross-cutting **feature interfaces** via composition |
| `mixin` | Inject an **abstract base class** that provides default behavior |
| `stubClass` | Wire up **stub serialization** for index-time access |

**Why not hand-write PSI classes?** Because the grammar *will* change. New proto
editions, custom options, AIP annotations — each would require editing parser,
PSI interface, implementation, stub type, and tests in lockstep. With the
current approach, changing a grammar rule automatically regenerates the PSI
class; behavior stays in mixins and feature interfaces that don't need to be
touched unless semantics change.

**Why not put all logic in the generated class?** Grammar-Kit generates
boilerplate accessors (`fun identifier()`, `fun messageBody()`). Semantic
behavior — name resolution, stub data extraction, reference provision — belongs
in mixins that survive regeneration.

### Decision 2: Feature Interfaces as Cross-Cutting Concerns

The `lang/psi/feature/` package defines roughly a dozen small interfaces:

| Interface | Concern |
|-----------|---------|
| `NamedElement` | Anything with a user-visible name |
| `ReferenceElement` | Anything that *refers to* another element |
| `QualifiedElement<T>` | Dotted name chains (`a.b.c`) with root/leaf traversal |
| `BodyOwner` / `BodyElement` | Elements with `{}`-delimited bodies |
| `DocumentOwner` / `DocumentElement` | Doc-comment association |
| `LookupableElement` | Completion item generation |
| `FoldingElement` | Code folding regions |
| `ValueElement<T>` / `ValueAssign` | Option value typing |

**Why separate interfaces instead of one fat base class?** Because the
combinations are irregular. A `MessageDefinition` is a `BodyOwner`, a
`NamedElement`, and a `DocumentOwner`. A `FieldDefinition` is a `NamedElement`
and an `OptionOwner` but *not* a `BodyOwner` (unless it has inline options). An
`ImportStatement` is none of these — it's a `ReferenceElement` pointing at a
file. Interfaces let the grammar declare exactly the set of capabilities each
rule has, and IDE features (folding provider, documentation provider, structure
view) can query for a single interface without knowing the concrete type.

**What would break without them?** Every IDE feature would need a chain of
`instanceof` checks against concrete PSI classes. Adding a new definition type
(say, `edition`) would require updating every consumer. With feature interfaces,
you implement `NamedElement` on the new type and the rename refactoring picks it
up automatically.

### Decision 3: Dual PSI Hierarchies Unified by Shared Features

Proto (`.proto`) and prototext (`.textproto`) have separate grammars, separate
parsers, and separate PSI element hierarchies (`Protobuf*` vs `ProtoText*`).
Yet both need field references, value assignment, documentation, and completion.

Rather than forcing them into one AST, the plugin gives each its own root
interface (`ProtobufElement`, `ProtoTextElement`) and its own mixin package,
but has both implement the *same* feature interfaces from `lang/psi/feature/`.
IDE features that operate on `NamedElement` or `ReferenceElement` work across
both formats without knowing which grammar produced the tree.

**Why not one grammar?** The syntaxes differ enough (comments, field assignment
syntax, lack of type definitions in textproto) that merging them would make the
BNF harder to read and the parser error-recovery worse. Separate grammars keep
each parser simple; shared feature interfaces keep the IDE layer unified.

### Decision 4: Stub-Backed Definitions for Index-Time Speed

Eleven element types (messages, fields, enums, services, etc.) carry stub
support. Each mixin provides a `stubData()` method that serializes just enough
information — typically a name and optionally a resource type — so the IDE can
populate `ShortNameIndex` and `QualifiedNameIndex` without parsing the file.

```kotlin
// Inside ProtobufMessageDefinitionMixin
override fun stubData(): Array<String> {
    return arrayOf(name() ?: "", resourceType() ?: "")
}
```

Every stub-based mixin has **dual constructors** — one for AST mode (during
parsing), one for stub mode (during index loading). This means a
`ProtobufMessageDefinition` can answer `name()` and `qualifiedName()` whether
it was parsed from source or deserialized from the stub cache.

**Why does this matter?** Completion and "find usages" must scan the entire
project. Without stubs, that means parsing every `.proto` file. With stubs,
the IDE reads a compact binary index and only parses files the user actually
opens.

### Decision 5: Extension Functions as a Third Behavior Layer

Not all behavior fits neatly into mixins. Kotlin extension functions in
files like `ProtobufTypeName.kt`, `ProtobufFieldName.kt`, and
`ProtobufImportStatement.kt` add domain-specific methods
(`ProtobufTypeName.absolutely()`, `ProtobufImportStatement.public()`) without
polluting the mixin class hierarchy.

This creates a clean three-tier model:

1. **Feature interfaces** — contracts (`NamedElement`, `BodyOwner`)
2. **Mixins** — default implementations injected at generation time
3. **Extension functions** — domain helpers that anyone can call

Extensions are preferred when the behavior is a pure query that doesn't need
to override a generated method or participate in the stub lifecycle.

## How It Fits Together

```
 Grammar (BNF)
   │  generates PSI interfaces + Impl classes
   │  stitches in:  implements ──▶ Feature Interfaces
   │                mixin ──────▶ Mixin Base Classes
   │                stubClass ──▶ Stub Definitions
   ▼
 PSI Tree  ◀── parsed from source (AST mode)
   │           or deserialized from cache (stub mode)
   │
   ├─▶ References (created by mixins via getReferences())
   │     └─▶ ProtobufSymbolResolver
   │           walks scope chain upward for relative names,
   │           walks import stack for cross-file names,
   │           filters results by context (field type vs extend target)
   │
   ├─▶ Stubs (serialized by stubData(), indexed into ShortName / QualifiedName)
   │     └─▶ Completion queries index, then enriches with scope-local items
   │
   └─▶ IDE Features
         query feature interfaces:
           FoldingElement  ──▶ folding provider
           DocumentOwner   ──▶ quick-doc provider
           LookupableElement ──▶ completion contributor
           NamedElement    ──▶ rename refactoring
```

## Key Insight

The grammar declares *what* each PSI element *is* (its capabilities); mixins
and extensions define *how* it behaves. This separation means a grammar change
regenerates structure without losing semantics, and a new IDE feature can target
a feature interface without knowing every concrete element type. The entire
design is optimized for **independent evolution** of syntax, semantics, and
tooling.
