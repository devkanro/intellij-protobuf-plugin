package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.intellij.psi.util.parentsOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
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

fun ProtoTextFieldName.message(): ProtobufScope? {
    val field = parent as? ProtoTextField ?: return null
    val message = field.parent as? ProtoTextMessage ?: return null
    val parent = message.parent ?: return null

    if (parent is ProtoTextFile) {
        return parent.message()
    }

    val parentField =
        when (val parentAssign = parent.parentsOfType<ValueAssign>()) {
            is ProtobufOptionAssign -> parentAssign.field()

            is ProtoTextField -> {
                if (parentAssign.fieldName.isAny()) {
                    return parentAssign.fieldName.reference?.resolve() as? ProtobufScope
                }

                parentAssign.field()
            }

            else -> return null
        } ?: return null

    if (parentField is ProtobufScope) return parentField
    if (parentField is ProtobufFieldDefinition) return parentField.typeName.reference?.resolve() as? ProtobufScope

    return null
}
