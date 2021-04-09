package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.enum
import io.kanro.idea.plugin.protobuf.lang.psi.field
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufLookupItem

class ProtobufEnumValueReference(field: ProtobufEnumValue) :
    PsiReferenceBase<ProtobufEnumValue>(field) {
    override fun resolve(): PsiElement? {
        return element.enum()?.definitions()?.firstOrNull {
            it.name() == element.text
        }
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(0, element.textLength)
    }

    override fun getVariants(): Array<Any> {
        return element.enum()?.definitions()?.mapNotNull {
            if (it !is ProtobufEnumValueDefinition) return@mapNotNull null
            (it as? ProtobufLookupItem)?.lookup() ?: it
        }?.toTypedArray() ?: arrayOf()
    }
}
