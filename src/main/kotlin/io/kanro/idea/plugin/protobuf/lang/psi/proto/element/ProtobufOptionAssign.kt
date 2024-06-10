package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike

interface ProtobufOptionAssign : ValueAssign {
    override fun field(): ProtobufFieldLike? {
        return findChild<ProtobufOptionName>()?.resolve() as? ProtobufFieldLike
    }
}
