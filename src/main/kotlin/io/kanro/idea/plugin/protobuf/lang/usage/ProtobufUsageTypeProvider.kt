package io.kanro.idea.plugin.protobuf.lang.usage

import com.intellij.psi.PsiElement
import com.intellij.usages.impl.rules.UsageType
import com.intellij.usages.impl.rules.UsageTypeProvider
import io.kanro.idea.plugin.protobuf.ProtobufBundle
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufBuiltInOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtensionOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcIO
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufSymbolName

class ProtobufUsageTypeProvider : UsageTypeProvider {
    override fun getUsageType(element: PsiElement): UsageType? {
        return when (element) {
            is ProtobufFieldName -> ASSIGN_USAGE_TYPE
            is ProtobufBuiltInOptionName -> OPTION_USAGE_TYPE
            is ProtobufSymbolName -> when (element.parent.parent) {
                is ProtobufRpcIO -> METHOD_PARAMETER_USAGE_TYPE
                is ProtobufExtensionOptionName -> OPTION_USAGE_TYPE
                is ProtobufFieldDefinition, is ProtobufMapFieldDefinition -> FIELD_DECLARATION_USAGE_TYPE
                is ProtobufExtendDefinition -> EXTEND_DECLARATION_USAGE_TYPE
                else -> null
            }
            else -> null
        }
    }

    companion object {
        val FIELD_DECLARATION_USAGE_TYPE = UsageType(ProtobufBundle.messagePointer("usage.type.field.declaration"))
        val METHOD_PARAMETER_USAGE_TYPE = UsageType(ProtobufBundle.messagePointer("usage.type.method.parameter"))
        val EXTEND_DECLARATION_USAGE_TYPE = UsageType(ProtobufBundle.messagePointer("usage.type.extend.declaration"))
        val OPTION_USAGE_TYPE = UsageType(ProtobufBundle.messagePointer("usage.type.option"))
        val ASSIGN_USAGE_TYPE = UsageType(ProtobufBundle.messagePointer("usage.type.assign"))
    }
}
