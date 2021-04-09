package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufLookupItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufQuickNavigateInfo
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.util.DocumentScope
import io.kanro.idea.plugin.protobuf.lang.util.doc

abstract class ProtobufDefinitionBase(node: ASTNode) :
    ProtobufElementBase(node),
    ProtobufDefinition,
    ItemPresentation,
    ProtobufLookupItem,
    ProtobufQuickNavigateInfo {
    override fun owner(): ProtobufScope? {
        return parentOfType()
    }

    override fun identifier(): ProtobufIdentifier? {
        return findChildByClass(ProtobufIdentifier::class.java)
    }

    override fun getNameIdentifier(): PsiElement? {
        return identifier()
    }

    override fun getTextOffset(): Int {
        return identifier()?.textOffset ?: super.getTextOffset()
    }

    override fun name(): String? {
        return identifier()?.text
    }

    override fun qualifiedName(): QualifiedName? {
        return owner()?.scope()?.append(name() ?: return null)
    }

    override fun getNavigationElement(): PsiElement {
        return identifier() ?: this
    }

    override fun getName(): String? {
        return name()
    }

    override fun setName(name: String): PsiElement {
        (identifier()?.node as? LeafElement)?.replaceWithText(name)
        return this
    }

    override fun getPresentation(): ItemPresentation? {
        return this
    }

    override fun getPresentableText(): String? {
        return name()
    }

    override fun getLocationString(): String? {
        return owner()?.scope()?.toString()
    }

    override fun lookup(): LookupElementBuilder? {
        return LookupElementBuilder.create(name() ?: return null)
            .withTypeText(type())
            .withIcon(getIcon(false))
            .withPsiElement(this)
    }

    override fun navigateInfo(): String {
        return doc {
            navigateInfoLocal(this)
            navigateInfoDefinition(this)
        }
    }

    protected open fun navigateInfoLocal(scope: DocumentScope) {
        scope.apply {
            link {
                locationString?.let {
                    text("$it ")
                }
            }
            italic(file().name())
        }
    }

    protected open fun navigateInfoDefinition(scope: DocumentScope) {
        scope.apply {
            definition {
                text("${type()} ")
                bold(name())
            }
        }
    }
}
