package io.kanro.idea.plugin.protobuf.aip.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldAssign
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.forEach
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.realItems
import io.kanro.idea.plugin.protobuf.lang.psi.stringRangeInParent
import io.kanro.idea.plugin.protobuf.lang.psi.value

class ProtobufRpcInputFieldReference(field: ProtobufStringValue) :
    ProtobufFieldReferenceInString(field) {
    override fun message(): ProtobufMessageDefinition? {
        val rpc = element.parentOfType<ProtobufOptionOwner>() as? ProtobufRpcDefinition ?: return null
        return rpc.input()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition
    }
}

class ProtobufRpcOutputFieldReference(field: ProtobufStringValue) :
    ProtobufFieldReferenceInString(field) {
    override fun message(): ProtobufMessageDefinition? {
        val rpc = element.parentOfType<ProtobufOptionOwner>() as? ProtobufRpcDefinition ?: return null
        return rpc.output()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition
    }
}

abstract class ProtobufFieldReferenceInString(field: ProtobufStringValue) :
    PsiReferenceBase<ProtobufStringValue>(field) {
    abstract fun message(): ProtobufMessageDefinition?

    override fun resolve(): PsiElement? {
        val fieldText = element.value() ?: return null
        message()?.forEach {
            if (it is ProtobufFieldLike && it.fieldName() == fieldText) {
                return it
            }
        }
        return null
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return element.stringRangeInParent()
    }

    override fun getVariants(): Array<Any> {
        return message()?.realItems()?.mapNotNull {
            (it as? ProtobufFieldLike)?.lookup()?.let {
                when (element.parent) {
                    is ProtobufFieldAssign -> it.withInsertHandler(insertHandler)
                    is ProtobufOptionName -> it.withInsertHandler(insertHandler)
                    else -> it
                }
            }
        }?.toTypedArray() ?: arrayOf()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        (element.stringLiteral.node as? LeafElement)?.replaceWithText("\"$newElementName\"")
        return element
    }

    companion object {
        private val insertHandler = SmartInsertHandler("\"")
    }
}
