package io.kanro.idea.plugin.protobuf.buf.schema.common

import io.kanro.idea.plugin.protobuf.buf.schema.BufArraySchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufObjectSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufRootSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType

object BufLock : BufRootSchema {
    override val name: String = "buf.lock"

    override val type = BufObjectSchema(
        listOf(
            BufVersionFieldSchema,
            BufLockDepsFieldSchema
        )
    )
}

object BufLockDepsFieldSchema : BufFieldSchema(
    "deps",
    "",
    BufArraySchema(BufLockDepsSchema),
    true
)

object BufLockDepsSchema : BufObjectSchema(
    listOf(
        BufLockDepsRemoteSchema,
        BufLockDepsOwnerSchema,
        BufLockDepsRepositorySchema,
        BufLockDepsCommitSchema
    )
)

object BufLockDepsRemoteSchema : BufFieldSchema(
    "remote",
    "",
    BufSchemaScalarType.IDENTIFIER,
    false
)

object BufLockDepsOwnerSchema : BufFieldSchema(
    "owner",
    "",
    BufSchemaScalarType.IDENTIFIER,
    false
)

object BufLockDepsRepositorySchema : BufFieldSchema(
    "repository",
    "",
    BufSchemaScalarType.IDENTIFIER,
    false
)

object BufLockDepsCommitSchema : BufFieldSchema(
    "commit",
    "",
    BufSchemaScalarType.IDENTIFIER,
    false
)
