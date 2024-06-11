package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.value.MessageValue

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
    val message = field.parent as? MessageValue ?: return null

    if (message is ProtoTextFile) {
        return message.schema()
    }

    val parentField = message.parentOfType<ProtoTextField>()?.fieldName?.resolve() ?: return null

    if (parentField is ProtobufScope) return parentField
    if (parentField is ProtobufFieldDefinition) return parentField.typeName.resolve() as? ProtobufScope

    return null
}
