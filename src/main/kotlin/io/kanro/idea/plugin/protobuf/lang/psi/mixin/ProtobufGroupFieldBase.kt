package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupField
import io.kanro.idea.plugin.protobuf.lang.util.DocumentScope
import javax.swing.Icon

abstract class ProtobufGroupFieldBase(node: ASTNode) :
    ProtobufReservableScopeWithExtensionDefinitionBase(node),
    ProtobufGroupField {
    override fun number(): Long? {
        return integerValue?.text?.toLong()
    }

    override fun numberElement(): PsiElement? {
        return integerValue
    }

    override fun scope(): QualifiedName? {
        return owner()?.scope()?.append(buildTypeString() ?: return null)
    }

    override fun type(): String {
        return "group"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.GROUP_FIELD
    }

    override fun names(): Set<String> {
        val name = super.name() ?: return setOf()
        return setOf(StringUtil.wordsToBeginFromLowerCase(name), name)
    }

    override fun name(): String? {
        return super.name()?.let { StringUtil.wordsToBeginFromLowerCase(it) }
    }

    private fun buildTypeString(): String? {
        return super.name()
    }

    override fun getPresentableText(): String? {
        return "${name()} = ${integerValue?.text}(group)"
    }

    override fun lookup(): LookupElementBuilder? {
        return super.lookup()?.withTailText(" = ${integerValue?.text}(group)", true)
    }

    override fun navigateInfoDefinition(scope: DocumentScope) {
        scope.apply {
            definition {
                text("group ")
                bold(buildTypeString())
                grayed(" = ${integerValue?.text}")
            }
        }
    }
}
