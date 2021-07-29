package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufDocumented
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufLookupItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.util.doc
import javax.swing.Icon

interface ProtobufPackageName :
    ProtobufNamedElement,
    ProtobufLookupItem,
    ProtobufDocumented,
    NavigatablePsiElement,
    ItemPresentation,
    PsiNameIdentifierOwner {
    override fun name(): String? {
        return nameIdentifier?.text
    }

    override fun getNameIdentifier(): PsiElement? {
        return this
    }

    override fun setName(name: String): PsiElement {
        (node as? LeafElement)?.replaceWithText(name)
        return this
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.PACKAGE
    }

    override fun getPresentableText(): String? {
        return name()
    }

    override fun getLocationString(): String? {
        val list = mutableListOf<String>()
        var item: PsiElement? = this.prevSibling
        while (item != null) {
            if (item is ProtobufPackageName) {
                list += item.name() ?: return null
            }
            item = item.prevSibling
        }
        list.reverse()
        return QualifiedName.fromComponents(list).toString()
    }

    override fun navigateInfo(): String? {
        return doc {
            link {
                locationString?.let {
                    text("$it ")
                }
            }
            italic(file().name())
            definition {
                text("package $presentableText")
            }
        }
    }

    override fun lookup(): LookupElementBuilder? {
        val name = name() ?: return null
        return LookupElementBuilder.create(name)
            .withTypeText("package")
            .withPresentableText(name)
            .withIcon(getIcon(false))
            .withPsiElement(this)
    }
}
