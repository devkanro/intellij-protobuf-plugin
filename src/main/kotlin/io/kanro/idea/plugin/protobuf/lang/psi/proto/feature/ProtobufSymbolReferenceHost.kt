package io.kanro.idea.plugin.protobuf.lang.psi.proto.feature

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement

interface ProtobufSymbolReferenceHost : ProtobufElement {
    fun referencesHover(): ProtobufSymbolReferenceHover? {
        return ProtobufSymbolReferenceProvider.hovers(this)
    }
}

interface ProtobufSymbolReferenceProvider {
    fun hovers(element: ProtobufSymbolReferenceHost): ProtobufSymbolReferenceHover?

    companion object : ProtobufSymbolReferenceProvider {
        var extensionPoint: ExtensionPointName<ProtobufSymbolReferenceProvider> =
            ExtensionPointName.create("io.kanro.idea.plugin.protobuf.symbolReferenceProvider")

        override fun hovers(element: ProtobufSymbolReferenceHost): ProtobufSymbolReferenceHover? {
            extensionPoint.extensionList.forEach {
                it.hovers(element)?.let {
                    return it
                }
            }
            return null
        }
    }
}

interface ProtobufSymbolReferenceHover {
    fun symbol(): QualifiedName {
        return QualifiedName.fromComponents(symbolParts().map { it.value })
    }

    fun textRange(): TextRange

    fun symbolParts(): List<SymbolPart>

    fun renamePart(
        index: Int,
        newName: String,
    )

    fun rename(newName: String)

    fun absolutely(): Boolean

    fun variantFilter(): PsiElementFilter

    data class SymbolPart(val startOffset: Int, val value: String)
}
