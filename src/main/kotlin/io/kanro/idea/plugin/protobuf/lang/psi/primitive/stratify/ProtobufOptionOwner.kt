package io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify

import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufOptionOwner : ProtobufElement {
    @JvmDefault
    fun options(): Array<ProtobufOptionHover> {
        return if (this is ProtobufBodyOwner) {
            body()?.findChildren() ?: arrayOf()
        } else {
            findChildren()
        }
    }
}
