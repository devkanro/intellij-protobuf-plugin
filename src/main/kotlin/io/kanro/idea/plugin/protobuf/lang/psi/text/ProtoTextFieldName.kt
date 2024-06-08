package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.intellij.psi.util.parentsOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope

fun ProtoTextFieldName.isSimpleField(): Boolean {
    return symbolName != null
}

fun ProtoTextFieldName.isExtensionField(): Boolean {
    return extensionName != null
}

fun ProtoTextFieldName.isAny(): Boolean {
    return anyName != null
}

fun ProtoTextFieldName.resolve(): ProtobufElement? {
    symbolName?.let {
        return reference?.resolve() as? ProtobufElement
    }

    extensionName?.let {
        return it.typeName.leaf().reference?.resolve() as? ProtobufElement
    }

    anyName?.let {
        return it.typeName.leaf().reference?.resolve() as? ProtobufElement
    }

    return null
}

fun ProtoTextFieldName.ownerMessage(): ProtobufScope? {
    val field = parent as? ProtoTextField ?: return null
    val message = field.parent as? ProtoTextMessageBody ?: return null
    val parent = message.parent ?: return null

    if (parent is ProtoTextFile) {
        return parent.message()
    }

    val parentField =
        when (val parentAssign = parent.parentsOfType<ValueAssign>()) {
            is ProtobufOptionAssign -> parentAssign.field()

            is ProtoTextField -> {
                parentAssign.fieldName.resolve()
            }

            else -> return null
        } ?: return null

    if (parentField is ProtobufScope) return parentField
    if (parentField is ProtobufFieldDefinition) return parentField.typeName.reference?.resolve() as? ProtobufScope

    return null
}
