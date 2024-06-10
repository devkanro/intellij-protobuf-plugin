package io.kanro.idea.plugin.protobuf.lang.psi.proto.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.items
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.proto.key
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ownerMessage
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.value
import io.kanro.idea.plugin.protobuf.lang.psi.realItems

class ProtobufFieldReference(field: ProtobufFieldName) :
    PsiReferenceBase<ProtobufFieldName>(field) {
    override fun resolve(): PsiElement? {
        val message = element.ownerMessage() ?: return null
        if (message is ProtobufMapFieldDefinition) {
            when (element.text) {
                "key" -> return message.key()
                "value" -> return message.value()
            }
        }
        message.items<ProtobufFieldLike> {
            if (it.fieldName() == element.text) {
                return it
            }
        }
        return null
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(0, element.textLength)
    }

    override fun getVariants(): Array<Any> {
        val message = element.ownerMessage() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        if (message is ProtobufMapFieldDefinition) {
            return message.entryFields().map {
                when (it.psiElement?.reference?.resolve()) {
                    is ProtobufGroupDefinition,
                    is ProtobufMessageDefinition,
                    -> it.withInsertHandler(messageFieldInsertHandler)

                    else -> it.withInsertHandler(fieldInsertHandler)
                }
            }.toTypedArray()
        }
        return message.realItems().mapNotNull {
            (it as? ProtobufFieldLike)?.lookup()?.let {
                when (it.psiElement?.reference?.resolve()) {
                    is ProtobufGroupDefinition,
                    is ProtobufMessageDefinition,
                    -> it.withInsertHandler(messageFieldInsertHandler)

                    else -> it.withInsertHandler(fieldInsertHandler)
                }
            }
        }.toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        ProtobufPsiFactory.createFieldName(element.project, newElementName).let {
            return element.replace(it)
        }
    }

    companion object {
        private val fieldInsertHandler = SmartInsertHandler(": ")

        private val messageFieldInsertHandler = SmartInsertHandler(" {}", -1)
    }
}
