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
