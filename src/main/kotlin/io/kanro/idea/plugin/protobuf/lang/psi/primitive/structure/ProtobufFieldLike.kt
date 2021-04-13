package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

interface ProtobufFieldLike : ProtobufDefinition, ProtobufNumbered {
    @JvmDefault
    fun fieldName(): String? {
        return name()
    }

    fun fieldType(): String?

    @JvmDefault
    override fun tailText(): String? {
        return ": ${fieldType()} = ${number()}"
    }
}
