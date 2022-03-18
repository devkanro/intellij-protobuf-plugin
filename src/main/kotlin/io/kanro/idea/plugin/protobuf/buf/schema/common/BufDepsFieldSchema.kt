package io.kanro.idea.plugin.protobuf.buf.schema.common

import io.kanro.idea.plugin.protobuf.buf.schema.BufArraySchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType

object BufDepsFieldSchema : BufFieldSchema(
    "deps",
    "The `deps` key is optional, and declares one or more modules that your module depends on. Each deps entry must be a module reference, and, is directly associated with a repository, as well as a reference, which is either a tag or commit.",
    BufArraySchema(BufSchemaScalarType.IDENTIFIER),
    true
)
