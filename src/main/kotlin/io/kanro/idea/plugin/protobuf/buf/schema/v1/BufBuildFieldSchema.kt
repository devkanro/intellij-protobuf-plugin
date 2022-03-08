package io.kanro.idea.plugin.protobuf.buf.schema.v1

import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema

object BufBuildFieldSchema : BufFieldSchema(
    "build",
    "The `build` key is optional, and is used to control how buf builds modules.",
    BufBuildSchema,
    true
)
