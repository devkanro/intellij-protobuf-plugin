package io.kanro.idea.plugin.protobuf.lang.psi.proto.structure

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.feature.DocumentOwner
import io.kanro.idea.plugin.protobuf.lang.psi.feature.LookupElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufIdentifier
import io.kanro.idea.plugin.protobuf.lang.util.doc
import javax.swing.Icon

interface ProtobufDefinition :
    ProtobufScopeItem,
    PsiNameIdentifierOwner,
    NamedElement,
    LookupElement,
    DocumentOwner,
    NavigatablePsiElement,
    ItemPresentation {
    fun type(): String

    fun identifier(): ProtobufIdentifier? {
        return findChild()
    }

    override fun name(): String? {
        return identifier()?.text
    }

    fun qualifiedName(): QualifiedName? {
        return owner()?.scope()?.append(name() ?: return null)
    }

    fun nameElement(): PsiElement? {
        return identifier()
    }

    override fun getNameIdentifier(): PsiElement? {
        return identifier()
    }

    override fun setName(name: String): PsiElement {
        (identifier()?.identifierLiteral?.node as? LeafElement)?.replaceWithText(name)
        return this
    }

    override fun lookup(name: String?): LookupElementBuilder? {
        val currentName = name() ?: return null
        return LookupElementBuilder.create(name ?: currentName)
            .withIcon(getIcon(false))
            .withPresentableText(currentName)
            .withTailText(tailText(), true)
            .withTypeText(type())
            .withPsiElement(this)
    }

    override fun getIcon(unused: Boolean): Icon? {
        return getIcon(0)
    }

    override fun getPresentableText(): String? {
        return "${name()}${tailText() ?: ""}"
    }

    override fun getLocationString(): String? {
        return owner()?.scope()?.toString()
    }

    fun tailText(): String? {
        return null
    }

    override fun navigateInfo(): String? {
        return doc {
            link {
                locationString?.let {
                    text("$it ")
                }
            }
            text(file().name())
            definition {
                text("${type()} $presentableText")
            }
        }
    }
}
