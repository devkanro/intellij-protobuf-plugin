package io.kanro.idea.plugin.protobuf.buf.schema.v1

import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufObjectSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufRootSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufGenManagedCcEnableArenasFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufGenManagedGoPackagePrefixFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufGenManagedJavaMultipleFilesFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufGenManagedJavaPackagePrefixFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufGenManagedJavaStringCheckUtf8FieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufGenManagedOptimizeForFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufGenManagedOverrideFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufGenPluginsFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufVersionFieldSchema

object BufGenYaml : BufRootSchema {
    override val name: String = "buf.gen.yaml"

    override val type = BufObjectSchema(
        listOf(
            BufVersionFieldSchema,
            BufGenPluginsFieldSchema,
            BufGenManagedFieldSchema
        )
    )
}

object BufGenManagedFieldSchema : BufFieldSchema(
    "managed",
    "The `managed` key is used to configure [managed mode](https://docs.buf.build/generate/managed-mode) and is an advanced feature.",
    BufGenManagedSchema,
    true
)

object BufGenManagedSchema : BufObjectSchema(
    listOf(
        BufGenManagedEnableFieldSchema,
        BufGenManagedCcEnableArenasFieldSchema,
        BufGenManagedJavaMultipleFilesFieldSchema,
        BufGenManagedJavaPackagePrefixFieldSchema,
        BufGenManagedJavaStringCheckUtf8FieldSchema,
        BufGenManagedOptimizeForFieldSchema,
        BufGenManagedGoPackagePrefixFieldSchema,
        BufGenManagedOverrideFieldSchema,
    )
)

object BufGenManagedEnableFieldSchema : BufFieldSchema(
    "enable",
    "The `enabled` key is **required** if any other managed keys are set. Setting enabled equal to true enables [managed mode](https://docs.buf.build/generate/managed-mode) according to [default behavior](https://docs.buf.build/generate/managed-mode#default-behavior).",
    BufSchemaScalarType.BOOL,
    false
)
