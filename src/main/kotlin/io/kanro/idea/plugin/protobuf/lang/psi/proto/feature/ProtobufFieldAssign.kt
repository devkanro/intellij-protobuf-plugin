package io.kanro.idea.plugin.protobuf.lang.psi.proto.feature

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.resolve
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike

interface ProtobufFieldAssign : ValueAssign {
    override fun field(): ProtobufFieldLike? {
        val field = findChild<ProtobufFieldName>() ?: return null
        return field.resolve() as? ProtobufFieldLike
    }
}
