package io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufConstant
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufOptionHover : ProtobufElement {
    fun option(): ProtobufOptionAssign? {
        return findChild()
    }

    fun isOption(extensionOptionName: QualifiedName): Boolean {
        val option = option() ?: return false
        val field =
            option.optionName.extensionOptionName?.typeName?.reference?.resolve() as? ProtobufFieldDefinition
                ?: return false
        return field.qualifiedName() == extensionOptionName
    }

    fun isOption(builtinOptionName: String): Boolean {
        val option = option() ?: return false
        return option.optionName.builtInOptionName?.textMatches(builtinOptionName) == true
    }

    fun value(): ProtobufConstant? {
        return option()?.findChild()
    }

    fun value(field: QualifiedName): ProtobufConstant? {
        if (field.componentCount == 0) return value()
        val optionName = option()?.optionName ?: return null
        var findName = field
        optionName.fieldNameList.forEach {
            if (!it.textMatches(findName.firstComponent ?: return null)) return null
            findName = findName.removeHead(1)
        }
        var value = value() ?: return null
        while (findName.componentCount != 0) {
            val messageValue = value.messageValue ?: return null
            value = messageValue.fieldAssignList.firstOrNull {
                it.fieldName.textMatches(findName.firstComponent ?: return null)
            }?.constant ?: return null
            findName = findName.removeHead(1)
        }
        return value
    }
}
