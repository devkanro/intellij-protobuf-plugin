package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.tree.LeafElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.enum
import io.kanro.idea.plugin.protobuf.lang.psi.forEach
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufLookupItem
import io.kanro.idea.plugin.protobuf.lang.psi.realItems

class ProtobufEnumValueReference(field: ProtobufEnumValue) :
    PsiReferenceBase<ProtobufEnumValue>(field) {
    override fun resolve(): PsiElement? {
        element.enum()?.forEach {
            if (it.name() == element.text) {
                return it
            }
        }
        return null
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(0, element.textLength)
    }

    override fun getVariants(): Array<Any> {
        return element.enum()?.realItems()?.mapNotNull {
            if (it !is ProtobufEnumValueDefinition) return@mapNotNull null
            (it as? ProtobufLookupItem)?.lookup()
        }?.toTypedArray() ?: arrayOf()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        (element.identifierLiteral?.node as? LeafElement)?.replaceWithText(newElementName)
        return element
    }
}
