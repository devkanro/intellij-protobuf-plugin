package io.kanro.idea.plugin.protobuf.aip.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.parentOfType
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.items
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.realItems
import io.kanro.idea.plugin.protobuf.lang.psi.stringRangeInParent
import io.kanro.idea.plugin.protobuf.lang.psi.text.feature.ProtoTextFieldAssign

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
        message()?.items<ProtobufFieldLike> {
            if (it.fieldName() == fieldText) {
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
                    is ProtoTextFieldAssign -> it.withInsertHandler(insertHandler)
                    is ProtobufOptionName -> it.withInsertHandler(insertHandler)
                    else -> it
                }
            }
        }?.toTypedArray() ?: ArrayUtilRt.EMPTY_OBJECT_ARRAY
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return element.replace(ProtobufPsiFactory.createStringValue(element.project, newElementName))
    }

    companion object {
        private val insertHandler = SmartInsertHandler("\"")
    }
}
