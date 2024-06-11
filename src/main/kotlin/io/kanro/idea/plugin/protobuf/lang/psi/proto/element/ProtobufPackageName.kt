package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.feature.DocumentOwner
import io.kanro.idea.plugin.protobuf.lang.psi.feature.LookupableElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.util.doc
import javax.swing.Icon

interface ProtobufPackageName :
    NamedElement,
    LookupableElement,
    DocumentOwner,
    NavigatablePsiElement,
    ItemPresentation,
    PsiNameIdentifierOwner,
    ProtobufElement {
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
        return ProtobufIcons.PACKAGE
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

    override fun lookup(name: String?): LookupElementBuilder? {
        val currentName = name() ?: return null
        return LookupElementBuilder.create(name ?: currentName)
            .withIcon(getIcon(false))
            .withPresentableText(currentName)
            .withTypeText("package")
            .withPsiElement(this)
    }
}
