package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign

fun ProtobufEnumValue.enum(): ProtobufEnumDefinition? {
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
