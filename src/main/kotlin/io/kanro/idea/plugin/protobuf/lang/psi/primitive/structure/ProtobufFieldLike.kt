package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

interface ProtobufFieldLike : ProtobufDefinition, ProtobufNumbered {
    fun fieldName(): String? {
        return name()
    }

    fun fieldType(): String?
    override fun tailText(): String? {
        return ": ${fieldType()} = ${number()}"
    }
}
