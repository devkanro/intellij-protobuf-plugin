package io.kanro.idea.plugin.protobuf.buf.schema.v1

import io.kanro.idea.plugin.protobuf.buf.schema.BufObjectSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufRootSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufBreakingFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufDepsFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufNameFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufVersionFieldSchema

object BufYaml : BufRootSchema {

    override val name: String = "buf.yaml"

    override val type = BufObjectSchema(
        listOf(
            BufVersionFieldSchema,
            BufNameFieldSchema,
            BufDepsFieldSchema,
            BufLintFieldSchema,
            BufBreakingFieldSchema
        )
    )
}
