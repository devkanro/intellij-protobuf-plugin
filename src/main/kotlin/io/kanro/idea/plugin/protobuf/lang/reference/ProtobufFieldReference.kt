package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.tree.LeafElement
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldAssign
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.forEach
import io.kanro.idea.plugin.protobuf.lang.psi.message
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.realItems

class ProtobufFieldReference(field: ProtobufFieldName) :
    PsiReferenceBase<ProtobufFieldName>(field) {

    override fun resolve(): PsiElement? {
        val message = element.message() ?: return null
        message.forEach {
            if (it is ProtobufFieldLike && it.fieldName() == element.text) {
                return it
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
            (it as? ProtobufFieldLike)?.lookup()?.let {
                when (element.parent) {
                    is ProtobufFieldAssign -> it.withInsertHandler(fieldInsertHandler)
                    is ProtobufOptionName -> it.withInsertHandler(optionInsertHandler)
                    else -> it
                }
            }
        }.toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        (element.identifierLiteral?.node as? LeafElement)?.replaceWithText(newElementName)
        return element
    }

    companion object {
        private val fieldInsertHandler = SmartInsertHandler(": ")
        private val optionInsertHandler = SmartInsertHandler(" = ")
    }
}
