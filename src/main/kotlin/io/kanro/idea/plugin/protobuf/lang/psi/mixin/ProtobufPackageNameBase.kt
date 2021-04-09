package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.source.tree.LeafElement
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufLookupItem
import javax.swing.Icon

abstract class ProtobufPackageNameBase(node: ASTNode) :
    ProtobufElementBase(node),
    ProtobufPackageName,
    ItemPresentation,
    PsiNameIdentifierOwner,
    ProtobufLookupItem {
    override fun getName(): String? {
        return identifierLiteral?.text
    }

    override fun setName(name: String): PsiElement {
        (identifierLiteral?.node as? LeafElement)?.replaceWithText(name)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return identifierLiteral
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.PACKAGE
    }

    override fun getPresentableText(): String? {
        return name
    }

    override fun getLocationString(): String? {
        return file().name
    }

    override fun lookup(): LookupElementBuilder? {
        return LookupElementBuilder.create(name ?: return null)
            .withTypeText("package")
            .withIcon(getIcon(false))
            .withPsiElement(this)
    }
}
