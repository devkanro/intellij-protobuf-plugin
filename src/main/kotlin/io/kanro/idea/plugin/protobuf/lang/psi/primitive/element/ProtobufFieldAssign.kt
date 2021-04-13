package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.forEach
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufValueAssign

interface ProtobufFieldAssign : ProtobufValueAssign {
    @JvmDefault
    override fun field(): ProtobufFieldDefinition? {
        val targetField = findChild<ProtobufFieldName>()?.text ?: return null
        val parentAssign = parentOfType<ProtobufValueAssign>() ?: return null
        val message = parentAssign.field()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition ?: return null

        message.forEach {
            if (it !is ProtobufFieldDefinition) return@forEach
            if (it.name() == targetField) return it
        }
        return null
    }
}
