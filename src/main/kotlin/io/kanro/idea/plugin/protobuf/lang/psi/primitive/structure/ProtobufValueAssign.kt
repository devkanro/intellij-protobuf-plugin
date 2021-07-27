package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufConstant
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufValueAssign : ProtobufElement {
    fun value(): ProtobufConstant? {
        return findChild()
    }

    fun field(): ProtobufFieldLike?
}
