package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.forEach
import io.kanro.idea.plugin.protobuf.lang.psi.message
import io.kanro.idea.plugin.protobuf.lang.psi.realItems

class ProtobufFieldReference(field: ProtobufFieldName) :
    PsiReferenceBase<ProtobufFieldName>(field) {

    override fun resolve(): PsiElement? {
        val message = element.message() ?: return null
        message.forEach {
            if (it.name() == element.text) {
                return when (it) {
                    is ProtobufFieldDefinition,
                    is ProtobufMapFieldDefinition,
                    is ProtobufGroupDefinition -> it
                    else -> null
                }
            }
        }
        return null
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(0, element.textLength)
    }

    override fun getVariants(): Array<Any> {
        val message = element.message() ?: return arrayOf()
        return message.realItems().mapNotNull {
            when (it) {
                is ProtobufFieldDefinition,
                is ProtobufMapFieldDefinition,
                is ProtobufGroupDefinition -> it.lookup()
                else -> null
            }
        }.toTypedArray()
    }
}
