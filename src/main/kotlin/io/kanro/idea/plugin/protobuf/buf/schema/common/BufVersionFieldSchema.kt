package io.kanro.idea.plugin.protobuf.buf.schema.common

import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumTypeSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumValueSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema

object BufVersionFieldSchema : BufFieldSchema(
    "version",
    "The `version` key is required, and defines the current configuration version. The only accepted values are `v1beta1` and `v1`.",
    BufEnumTypeSchema(listOf(BufEnumValueSchema("v1", ""), BufEnumValueSchema("v1beta1", ""))),
    false
)
