package io.kanro.idea.plugin.protobuf.lang.psi.proto.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.lang.psi.feature.LookupableElement
import io.kanro.idea.plugin.protobuf.lang.psi.items
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.enum
import io.kanro.idea.plugin.protobuf.lang.psi.realItems

class ProtobufEnumValueReference(field: ProtobufEnumValue) :
    PsiReferenceBase<ProtobufEnumValue>(field) {
    override fun resolve(): PsiElement? {
        element.enum()?.items<ProtobufEnumValueDefinition> {
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
            (it as? LookupableElement)?.lookup()
        }?.toTypedArray() ?: ArrayUtilRt.EMPTY_OBJECT_ARRAY
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        (element.identifierLiteral?.node as? LeafElement)?.replaceWithText(newElementName)
        return element
    }
}
