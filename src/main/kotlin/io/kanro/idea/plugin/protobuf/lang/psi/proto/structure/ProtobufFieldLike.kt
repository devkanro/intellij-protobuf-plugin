package io.kanro.idea.plugin.protobuf.lang.psi.proto.structure

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ProtobufNumbered

interface ProtobufFieldLike : ProtobufDefinition, ProtobufNumbered {
    fun fieldName(): String? {
        return name()
    }

    fun fieldType(): String?

    override fun tailText(): String? {
        return ": ${fieldType()} = ${number()}"
    }
}
