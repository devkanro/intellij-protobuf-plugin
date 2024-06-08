package io.kanro.idea.plugin.protobuf.lang.usage

import com.intellij.psi.PsiElement
import com.intellij.usages.impl.rules.UsageType
import com.intellij.usages.impl.rules.UsageTypeProvider
import io.kanro.idea.plugin.protobuf.ProtobufBundle
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcIO
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFieldName

class ProtobufUsageTypeProvider : UsageTypeProvider {
    override fun getUsageType(element: PsiElement): UsageType? {
        return when (element) {
            is ProtoTextFieldName -> ASSIGN_USAGE_TYPE
            is ProtobufOptionName -> OPTION_USAGE_TYPE
            is ProtobufTypeName ->
                when (element.parent) {
                    is ProtobufRpcIO -> METHOD_PARAMETER_USAGE_TYPE
                    is ProtobufFieldDefinition, is ProtobufMapFieldDefinition -> FIELD_DECLARATION_USAGE_TYPE
                    is ProtobufExtendDefinition -> EXTEND_DECLARATION_USAGE_TYPE
                    else -> null
                }

            else -> null
        }
    }
}

val FIELD_DECLARATION_USAGE_TYPE = UsageType(ProtobufBundle.messagePointer("usage.type.field.declaration"))
val METHOD_PARAMETER_USAGE_TYPE = UsageType(ProtobufBundle.messagePointer("usage.type.method.parameter"))
val EXTEND_DECLARATION_USAGE_TYPE = UsageType(ProtobufBundle.messagePointer("usage.type.extend.declaration"))
val OPTION_USAGE_TYPE = UsageType(ProtobufBundle.messagePointer("usage.type.option"))
val ASSIGN_USAGE_TYPE = UsageType(ProtobufBundle.messagePointer("usage.type.assign"))
