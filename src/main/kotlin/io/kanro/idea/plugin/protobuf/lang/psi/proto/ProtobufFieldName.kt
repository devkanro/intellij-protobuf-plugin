package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.value.MessageValue

fun ProtobufFieldName.resolve(): ProtobufElement? {
    symbolName?.let {
        return reference?.resolve() as? ProtobufElement
    }

    extensionName?.let {
        return it.extensionFieldName.leaf().reference?.resolve() as? ProtobufElement
    }

    anyName?.let {
        return it.typeName.leaf().reference?.resolve() as? ProtobufElement
    }

    return null
}

fun ProtobufFieldName.ownerMessage(): ProtobufScope? {
    val field = parent as? ProtobufField ?: return null
    val message = field.parent as? MessageValue ?: return null

    val parentField =
        when (val parentAssign = message.parentOfType<ValueAssign>()) {
            is ProtobufOptionAssign -> parentAssign.field()

            is ProtobufField -> {
                parentAssign.fieldName.resolve()
            }

            else -> return null
        } ?: return null

    if (parentField is ProtobufScope) return parentField
    if (parentField is ProtobufFieldDefinition) return parentField.typeName.resolve() as? ProtobufScope

    return null
}
