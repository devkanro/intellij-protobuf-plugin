package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.field
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufValueAssign

interface ProtobufOptionAssign : ProtobufValueAssign {
    @JvmDefault
    override fun field(): ProtobufFieldLike? {
        return findChild<ProtobufOptionName>()?.field()
    }
}
