package io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify

import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufBodyOwner : ProtobufElement {
    @JvmDefault
    fun body(): ProtobufBody? {
        return findChild()
    }
}
