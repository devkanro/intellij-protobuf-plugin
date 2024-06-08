package io.kanro.idea.plugin.protobuf.lang.psi.text.feature

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextField
import io.kanro.idea.plugin.protobuf.lang.psi.text.resolve

interface ProtoTextFieldAssign : ValueAssign {
    override fun field(): ProtobufFieldLike? {
        val field = findChild<ProtoTextField>() ?: return null
        return field.fieldName.resolve() as? ProtobufFieldLike
    }
}
