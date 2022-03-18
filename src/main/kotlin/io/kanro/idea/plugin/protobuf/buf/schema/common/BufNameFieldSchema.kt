package io.kanro.idea.plugin.protobuf.buf.schema.common

import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType

object BufNameFieldSchema : BufFieldSchema(
    "name",
    "The `name` is optional, and uniquely identifies your module. The name must be a valid module name and is directly associated with the repository that owns it.",
    BufSchemaScalarType.IDENTIFIER,
    true
)
