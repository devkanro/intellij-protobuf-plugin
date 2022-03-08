package io.kanro.idea.plugin.protobuf.buf.schema.common

import io.kanro.idea.plugin.protobuf.buf.schema.BufObjectSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufRootSchema

object BufEmptyYaml : BufRootSchema {
    override val name: String = "buf.yaml"

    override val type = BufObjectSchema(
        listOf(
            BufVersionFieldSchema
        )
    )
}

object BufEmptyLock : BufRootSchema {
    override val name: String = "buf.lock"

    override val type = BufObjectSchema(
        listOf(
            BufVersionFieldSchema
        )
    )
}

object BufEmptyGenYaml : BufRootSchema {
    override val name: String = "buf.gen.yaml"

    override val type = BufObjectSchema(
        listOf(
            BufVersionFieldSchema
        )
    )
}
