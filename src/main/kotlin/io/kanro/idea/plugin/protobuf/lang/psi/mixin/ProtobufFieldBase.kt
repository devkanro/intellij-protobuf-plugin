package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendBody
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.util.DocumentScope
import javax.swing.Icon

abstract class ProtobufFieldBase(node: ASTNode) :
    ProtobufLeafDefinitionBase(node),
    ProtobufFieldDefinition {
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
        return if (extend()) Icons.EXTEND_FIELD else Icons.FIELD
    }

    private fun buildTypeString(): String? {
        return typeName.symbolNameList.lastOrNull()?.text
    }

    private fun extend(): Boolean {
        return parent is ProtobufExtendBody
    }

    override fun getPresentableText(): String? {
        return "${name()}: ${buildTypeString()} = ${integerValue?.text}"
    }

    override fun lookup(): LookupElementBuilder? {
        return super.lookup()?.withTailText(": ${buildTypeString()} = ${integerValue?.text}", true)
    }

    override fun navigateInfoDefinition(scope: DocumentScope) {
        scope.apply {
            definition {
                val label = this@ProtobufFieldBase.fieldLabel
                if (label != null) {
                    text("${label.text} ")
                }
                bold(name())
                grayed(": ${buildTypeString()} = ${integerValue?.text}")
            }
        }
    }
}
