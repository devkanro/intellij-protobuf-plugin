package io.kanro.idea.plugin.protobuf.lang.psi.proto.feature

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.value.MessageValue

interface ProtobufOptionHover : ProtobufElement {
    fun option(): ProtobufOptionAssign? {
        return findChild()
    }

    fun isOption(extensionOptionName: QualifiedName): Boolean {
        val option = option() ?: return false
        return option.optionName.optionFieldNameList.firstOrNull()
            ?.extensionFieldName?.textMatches(extensionOptionName.toString()) == true
    }

    fun isOption(builtinOptionName: String): Boolean {
        val option = option() ?: return false
        return option.optionName.optionFieldNameList.firstOrNull()
            ?.symbolName?.textMatches(builtinOptionName) == true
    }

    fun value(): Any? {
        return option()?.constant?.value()
    }

    fun value(field: QualifiedName): Any? {
        if (field.componentCount == 0) return value()

        val optionName = option()?.optionName ?: return null
        var findName = field

        optionName.optionFieldNameList.forEach {
            if (!it.textMatches(findName.firstComponent ?: return null)) return null
            findName = findName.removeHead(1)
        }
        val value = value() ?: return null
        if (findName.componentCount == 0) return value

        if (value is MessageValue) {
            return value.value(findName)
        }

        return null
    }
}
