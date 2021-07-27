package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufDocumented
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufLookupItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.util.doc
import javax.swing.Icon

interface ProtobufScopeItem :
    ProtobufNamedElement,
    ProtobufLookupItem,
    ProtobufDocumented,
    NavigatablePsiElement,
    ItemPresentation {
    fun nameElement(): PsiElement?

    fun type(): String
    fun owner(): ProtobufScope? {
        return parentOfType()
    }

    override fun qualifiedName(): QualifiedName? {
        return owner()?.scope()?.append(name() ?: return null)
    }

    override fun externalQualifiedName(id: String): QualifiedName? {
        return owner()?.externalScope(id)?.append(externalName(id) ?: return null)
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
