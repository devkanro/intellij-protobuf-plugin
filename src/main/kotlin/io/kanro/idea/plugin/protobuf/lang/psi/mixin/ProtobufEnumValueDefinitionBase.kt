package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.util.DocumentScope
import javax.swing.Icon

abstract class ProtobufEnumValueDefinitionBase(node: ASTNode) :
    ProtobufLeafDefinitionBase(node),
    ProtobufEnumValueDefinition {

    override fun number(): Long? {
        return integerValue?.text?.toLong()
    }

    override fun numberElement(): PsiElement? {
        return integerValue
    }

    override fun type(): String {
        return "enum value"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.ENUM_VALUE
    }

    override fun getPresentableText(): String? {
        return "${name()} = ${integerValue?.text}"
    }

    override fun lookup(): LookupElementBuilder? {
        return super.lookup()?.withTailText(" = ${integerValue?.text}", true)
    }

    override fun navigateInfoDefinition(scope: DocumentScope) {
        scope.apply {
            definition {
                bold(name())
                grayed(" = ${integerValue?.text}")
            }
        }
    }
}
