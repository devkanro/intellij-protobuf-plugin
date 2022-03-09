package io.kanro.idea.plugin.protobuf.buf.schema.common

import io.kanro.idea.plugin.protobuf.buf.schema.BufArraySchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumTypeSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumValueSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufObjectSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType

object BufGenPluginsFieldSchema : BufFieldSchema(
    "plugins",
    "Each entry in the `buf.gen.yaml` `plugins` key is a `protoc` plugin configuration, which is a program that generates code by interacting with the compiled representation of your module.",
    BufArraySchema(BufGenPluginsSchema),
    true
)

object BufGenPluginsSchema : BufObjectSchema(
    listOf(
        BufGenPluginsNameFieldSchema,
        BufGenPluginsRemoteFieldSchema,
        BufGenPluginsOutFieldSchema,
        BufGenPluginsOptFieldSchema,
        BufGenPluginsPathFieldSchema,
        BufGenPluginsStrategyFieldSchema
    )
)

object BufGenPluginsNameFieldSchema : BufFieldSchema(
    "name",
    "One of `name` or `remote` for a plugin is **required**.\n\nIn the case of `<name>`, it is equal to the value in `protoc-gen-<name>`, which is the traditional naming convention for `protoc` plugins. To be clear, all `protoc` plugins begin with the `protoc-gen-` prefix. For example, the protoc-gen-go plugins.\n\nBy default, a `protoc-gen-<name>` program is expected to be on your `PATH` so that it can be discovered and executed by `buf`. This can be overridden with the path option shown below.",
    BufSchemaScalarType.IDENTIFIER,
    true
)

object BufGenPluginsRemoteFieldSchema : BufFieldSchema(
    "remote",
    "One of `name` or `remote` for a plugin is **required**.\n\nIn the case of `<remote>`, this enables you to run `buf generate` with a remote plugin, using the fully qualified path to the remote plugin defined via the BSR, `<remote>/<owner>/plugins/<plugin-name>:<plugin-version>`.",
    BufSchemaScalarType.IDENTIFIER,
    true
)

object BufGenPluginsOutFieldSchema : BufFieldSchema(
    "out",
    "The `out` of a plugin is **required**, and controls where the generated files are deposited for a given plugin. Although absolute paths are supported, this configuration is traditionally a relative output directory that depends on where `buf generate` is run.",
    BufSchemaScalarType.IDENTIFIER,
    false
)

object BufGenPluginsOptFieldSchema : BufFieldSchema(
    "opt",
    "The `opt` of a plugin is **optional**, and specifies one or more `protoc` plugin options for each plugin independently.",
    BufSchemaScalarType.IDENTIFIER,
    false
)

object BufGenPluginsPathFieldSchema : BufFieldSchema(
    "path",
    "The `path` of a plugin is **optional**, and overrides the default location and explicitly specify where to locate the `protoc` plugin.",
    BufSchemaScalarType.IDENTIFIER,
    false
)

object BufGenPluginsStrategyFieldSchema : BufFieldSchema(
    "strategy",
    "Your `strategy` of a plugin is **optional**, and specifies the generation `strategy` for `buf generate` to use.",
    BufEnumTypeSchema(
        listOf(
            BufEnumValueSchema(
                "directory",
                "This results in buf splitting the input files by directory and making separate plugin invocations in parallel."
            ),
            BufEnumValueSchema("all", "This results in buf making a single plugin invocation with all input files.")
        )
    ),
    false
)

object BufGenManagedCcEnableArenasFieldSchema : BufFieldSchema(
    "cc_enable_arenas",
    "The `cc_enable_arenas` key is **optional**, and controls what the cc_enable_arenas value is set to in all of the files contained within the generation target input. The only accepted values are `false` and `true`.",
    BufSchemaScalarType.BOOL,
    true
)

object BufGenManagedJavaMultipleFilesFieldSchema : BufFieldSchema(
    "java_multiple_files",
    "The `java_multiple_files` key is **optional**, and controls what the java_multiple_files value is set to in all of the files contained within the generation target input. The only accepted values are `false` and `true`.",
    BufSchemaScalarType.BOOL,
    true
)

object BufGenManagedJavaPackagePrefixFieldSchema : BufFieldSchema(
    "java_package_prefix",
    "The `java_package_prefix` key is **optional**, and controls what the java_package prefix value is set to in all of the files contained within the generation target input. By default, the value is `com`.",
    BufSchemaScalarType.IDENTIFIER,
    true
)

object BufGenManagedJavaStringCheckUtf8FieldSchema : BufFieldSchema(
    "java_string_check_utf8",
    "The `java_string_check_utf8` key is **optional**, and controls what the java_string_check_utf8 value is set to in all of the files contained within the generation target input. The only accepted values are `false` and `true`.",
    BufSchemaScalarType.BOOL,
    true
)

object BufGenManagedOptimizeForFieldSchema : BufFieldSchema(
    "optimize_for",
    "The `optimize_for` key is **optional**, and controls what the optimize_for value is set to in all of the files contained within the generation target input. The only accepted values are `SPEED`, `CODE_SIZE` and `LITE_RUNTIME`. If omitted, the default value, `SPEED`, is used.",
    BufEnumTypeSchema(
        listOf(
            BufEnumValueSchema("SPEED", ""),
            BufEnumValueSchema("CODE_SIZE", ""),
            BufEnumValueSchema("LITE_RUNTIME", ""),
        )
    ),
    true
)

object BufGenManagedGoPackagePrefixFieldSchema : BufFieldSchema(
    "go_package_prefix",
    "The `go_package_prefix` key is **optional**, and controls what the go_package value is set to in all the files contained within the generation target input.",
    BufGenManagedGoPackagePrefixSchema,
    true
)

object BufGenManagedGoPackagePrefixSchema : BufObjectSchema(
    listOf(
        BufGenManagedGoPackagePrefixDefaultFieldSchema,
        BufGenManagedGoPackagePrefixExceptFieldSchema,
        BufGenManagedGoPackagePrefixOverrideFieldSchema
    )
)

object BufGenManagedGoPackagePrefixDefaultFieldSchema : BufFieldSchema(
    "default",
    "The `default` key is **required** if the `go_package_prefix` key is set. The `default` value is used as a prefix for the `go_package` value set in each of the files. The `default` value **must** be a relative filepath that **must not** jump context from the current directory, that is they must be subdirectories relative to the current working directory. As an example, `../external` is invalid.",
    BufSchemaScalarType.STRING,
    false
)

object BufGenManagedGoPackagePrefixExceptFieldSchema : BufFieldSchema(
    "except",
    "The `except` key is **optional**, and removes certain modules from the `go_package` file option override behavior. The `except` values **must** be valid [module names](/bsr/overview#modules).\n" +
        "\n" +
        "There are situations where you may want to enable [managed mode](/generate/managed-mode) for the `go_package` option in _most_ of your Protobuf files, but not necessarily for _all_ of your Protobuf files. This is particularly relevant for the `buf.build/googleapis/googleapis` module, which points its `go_package` value to an [external repository](https://github.com/googleapis/go-genproto). Popular libraries, such as [grpc-go](https://github.com/grpc/grpc-go) depend on these `go_package` values, so it's important that managed mode does not overwrite them.",
    BufArraySchema(BufSchemaScalarType.IDENTIFIER),
    true
)

object BufGenManagedGoPackagePrefixOverrideFieldSchema : BufFieldSchema(
    "override",
    "The `override` key is **optional**, and overrides the `go_package` file option value used for specific modules. The `override` keys **must** be valid module names. Additionally, the corresponding `override` values **must** be a valid [Go import path](https://golang.org/ref/spec#ImportPath) and **must not** jump context from the current directory. As an example, `../external` is invalid.\n" +
        "\n" +
        "This setting is used for [workspace](https://docs.buf.build/reference/workspaces) environments, where you have a module that imports from another module in the same workspace, and you need to generate the Go code for each module in different directories. This is particularly relevant for repositories that decouple their private API definitions from their public API definitions (as is the case for `buf`).",
    BufObjectSchema(listOf()),
    true
)

object BufGenManagedOverrideFieldSchema : BufFieldSchema(
    "override",
    "This is a list of per-file overrides for each modifier.",
    BufObjectSchema(listOf()),
    true
)
