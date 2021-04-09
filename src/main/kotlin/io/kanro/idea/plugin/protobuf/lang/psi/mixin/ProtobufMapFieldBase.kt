package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapField
import io.kanro.idea.plugin.protobuf.lang.util.DocumentScope
import javax.swing.Icon

abstract class ProtobufMapFieldBase(node: ASTNode) :
    ProtobufLeafDefinitionBase(node),
    ProtobufMapField {

    override fun number(): Long? {
        return integerValue?.text?.toLong()
    }

    override fun numberElement(): PsiElement? {
        return integerValue
    }

    override fun type(): String {
        return "field"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.FIELD
    }

    private fun buildTypeString(): String? {
        val typeNames = typeNameList
        if (typeNames.size != 2) return super.getPresentableText()
        val keyType = typeNames[0].symbolNameList.lastOrNull()?.text
        val valueType = typeNames[1].symbolNameList.lastOrNull()?.text
        return "map<$keyType, $valueType>"
    }

    override fun getPresentableText(): String? {
        return "${name()}: ${buildTypeString()}"
    }

    override fun lookup(): LookupElementBuilder? {
        return super.lookup()?.withTailText(": ${buildTypeString()}", true)
    }

    override fun navigateInfoDefinition(scope: DocumentScope) {
        scope.apply {
            definition {
                bold(name())
                grayed(": ${buildTypeString()} = ${integerValue?.text}")
            }
        }
    }
}
