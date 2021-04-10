package io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufOptionHover : ProtobufElement {
    @JvmDefault
    fun option(): ProtobufOptionAssign? {
        return findChild()
    }
}
