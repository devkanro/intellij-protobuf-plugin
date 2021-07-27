package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.source.tree.LeafElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.psi.findChild

interface ProtobufDefinition : ProtobufScopeItem, PsiNameIdentifierOwner {
    fun identifier(): ProtobufIdentifier? {
        return findChild()
    }

    override fun name(): String? {
        return identifier()?.text
    }

    override fun nameElement(): PsiElement? {
        return identifier()
    }

    override fun getNameIdentifier(): PsiElement? {
        return identifier()
    }

    override fun setName(name: String): PsiElement {
        (identifier()?.identifierLiteral?.node as? LeafElement)?.replaceWithText(name)
        return this
    }

    override fun lookup(): LookupElementBuilder? {
        val name = name() ?: return null
        return LookupElementBuilder.create(name)
            .withIcon(getIcon(false))
            .withPresentableText(name)
            .withTailText(tailText(), true)
            .withTypeText(type())
            .withPsiElement(this)
    }
}
