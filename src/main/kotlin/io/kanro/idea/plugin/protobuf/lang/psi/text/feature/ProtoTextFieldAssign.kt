package io.kanro.idea.plugin.protobuf.lang.psi.text.feature

import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueElement
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.text.resolve

interface ProtoTextFieldAssign : ValueAssign {
    override fun field(): ProtobufFieldLike? {
        val field = findChild<ProtoTextFieldName>() ?: return null
        return field.resolve() as? ProtobufFieldLike
    }

    override fun valueElement(): ValueElement<*>? {
        return findChild<ValueElement<*>>()
    }
}
