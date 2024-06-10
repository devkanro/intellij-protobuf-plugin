package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.key
import io.kanro.idea.plugin.protobuf.lang.psi.proto.value

fun ProtoTextEnumValue.enum(): ProtobufEnumDefinition? {
    val assign = parentOfType<ValueAssign>() ?: return null
    val field = assign.field() ?: return null

    return when (field) {
        is ProtobufFieldDefinition -> {
            field.typeName.resolve() as? ProtobufEnumDefinition
        }

        is ProtobufMapFieldDefinition -> {
            val targetField = assign.field()?.text ?: return null
            when (targetField) {
                "key" -> field.key()?.resolve() as? ProtobufEnumDefinition
                "value" -> field.value()?.resolve() as? ProtobufEnumDefinition
                else -> null
            }
        }

        else -> null
    }
}
