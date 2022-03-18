package io.kanro.idea.plugin.protobuf.buf.schema.v1

import io.kanro.idea.plugin.protobuf.buf.schema.BufArraySchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumTypeSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumValueSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufObjectSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufRootSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType

object BufWorkYaml : BufRootSchema {
    override val name: String = "buf.work.yaml"

    override val type = BufObjectSchema(
        listOf(
            BufWorkVersionFieldSchema,
            BufWorkDirectoriesFieldSchema
        )
    )
}

object BufEmptyWorkYaml : BufRootSchema {
    override val name: String = "buf.gen.yaml"

    override val type = BufObjectSchema(
        listOf(
            BufWorkVersionFieldSchema
        )
    )
}

object BufWorkVersionFieldSchema : BufFieldSchema(
    "version",
    "The `version` key is required, and defines the current configuration version. The only accepted value is `v1`.",
    BufEnumTypeSchema(listOf(BufEnumValueSchema("v1", ""))),
    false
)

object BufWorkDirectoriesFieldSchema : BufFieldSchema(
    "directories",
    "The `directories` key is **required**, and lists the directories that define modules to be included in the workspace. The directory paths must be relative to the `buf.work.yaml`, and cannot point to a location outside of your `buf.work.yaml`. For example, `../external` is invalid.",
    BufArraySchema(BufSchemaScalarType.IDENTIFIER),
    false
)
