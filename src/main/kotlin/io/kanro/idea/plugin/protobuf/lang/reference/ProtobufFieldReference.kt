package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.message
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufLookupItem

class ProtobufFieldReference(field: ProtobufFieldName) :
    PsiReferenceBase<ProtobufFieldName>(field) {

    override fun resolve(): PsiElement? {
        val message = element.message() ?: return null
        return message.definitions().firstOrNull {
            it.name() == element.text
        } as? ProtobufFieldDefinition
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(0, element.textLength)
    }

    override fun getVariants(): Array<Any> {
        val message = element.message() ?: return arrayOf()
        return message.definitions().mapNotNull {
            if (it !is ProtobufFieldDefinition) return@mapNotNull null
            (it as? ProtobufLookupItem)?.lookup() ?: it
        }.toTypedArray()
    }
}
