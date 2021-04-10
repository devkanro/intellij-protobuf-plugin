package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

interface ProtobufDocumented {
    @JvmDefault
    fun navigateInfo(): String? {
        return null
    }

    @JvmDefault
    fun document(): String? {
        return null
    }

    @JvmDefault
    fun hoverDocument(): String? {
        return document()
    }
}
