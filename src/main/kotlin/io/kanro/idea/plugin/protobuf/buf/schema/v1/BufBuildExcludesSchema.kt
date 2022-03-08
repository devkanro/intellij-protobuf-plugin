package io.kanro.idea.plugin.protobuf.buf.schema.v1

import io.kanro.idea.plugin.protobuf.buf.schema.BufArraySchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType

object BufBuildExcludesSchema : BufFieldSchema(
    "excludes",
    "The `excludes` key is optional, and lists directories to ignore from .proto file discovery. Any directories added to this list are completely skipped and excluded in the module. We do not recommend using this option in general, however in some situations it is unavoidable.",
    BufArraySchema(BufSchemaScalarType.STRING),
    true
)
