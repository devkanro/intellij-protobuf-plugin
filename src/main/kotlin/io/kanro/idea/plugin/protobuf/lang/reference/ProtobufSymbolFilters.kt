package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendBody
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupField
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapField
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOneOfField
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcMethod
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.resolve
import io.kanro.idea.plugin.protobuf.lang.support.Options
import io.kanro.idea.plugin.protobuf.lang.util.and
import io.kanro.idea.plugin.protobuf.lang.util.or

object ProtobufSymbolFilters {
    val packagePart = PsiElementFilter {
        it is ProtobufPackageName
    }

    fun extensionOptionName(owner: ProtobufOptionOwner?): PsiElementFilter {
        return when (owner) {
            is ProtobufFile -> fileExtensionOptionName
            is ProtobufMessageDefinition, is ProtobufGroupField -> messageExtensionOptionName
            is ProtobufFieldDefinition, is ProtobufMapField -> fieldExtensionOptionName
            is ProtobufOneOfField -> oneofExtensionOptionName
            is ProtobufEnumDefinition -> enumExtensionOptionName
            is ProtobufEnumValueDefinition -> enumValueExtensionOptionName
            is ProtobufServiceDefinition -> serviceExtensionOptionName
            is ProtobufRpcMethod -> methodExtensionOptionName
            else -> extensionOptionName
        }
    }

    private val extensionOptionName = PsiElementFilter {
        (it is ProtobufFieldDefinition || it is ProtobufGroupField) && it.parent is ProtobufExtendBody
    }
    private val fileExtensionOptionName = extensionOptionName and TargetOptionFilter(Options.FILE_OPTIONS)
    private val messageExtensionOptionName = extensionOptionName and TargetOptionFilter(Options.MESSAGE_OPTIONS)
    private val fieldExtensionOptionName = extensionOptionName and TargetOptionFilter(Options.FIELD_OPTIONS)
    private val oneofExtensionOptionName = extensionOptionName and TargetOptionFilter(Options.ONEOF_OPTIONS)
    private val enumExtensionOptionName = extensionOptionName and TargetOptionFilter(Options.ENUM_OPTIONS)
    private val enumValueExtensionOptionName = extensionOptionName and TargetOptionFilter(Options.ENUM_VALUE_OPTIONS)
    private val serviceExtensionOptionName = extensionOptionName and TargetOptionFilter(Options.SERVICE_OPTIONS)
    private val methodExtensionOptionName = extensionOptionName and TargetOptionFilter(Options.METHOD_OPTIONS)

    fun extensionOptionNameVariants(owner: ProtobufOptionOwner?): PsiElementFilter {
        return when (owner) {
            is ProtobufFile -> fileExtensionOptionNameVariants
            is ProtobufMessageDefinition, is ProtobufGroupField -> messageExtensionOptionNameVariants
            is ProtobufFieldDefinition, is ProtobufMapField -> fieldExtensionOptionNameVariants
            is ProtobufOneOfField -> oneofExtensionOptionNameVariants
            is ProtobufEnumDefinition -> enumExtensionOptionNameVariants
            is ProtobufEnumValueDefinition -> enumValueExtensionOptionNameVariants
            is ProtobufServiceDefinition -> serviceExtensionOptionNameVariants
            is ProtobufRpcMethod -> methodExtensionOptionNameVariants
            else -> extensionOptionNameVariants
        }
    }

    private val extensionOptionNameVariants = extensionOptionName or packagePart
    private val fileExtensionOptionNameVariants = fileExtensionOptionName or packagePart
    private val messageExtensionOptionNameVariants = messageExtensionOptionName or packagePart
    private val fieldExtensionOptionNameVariants = fieldExtensionOptionName or packagePart
    private val oneofExtensionOptionNameVariants = oneofExtensionOptionName or packagePart
    private val enumExtensionOptionNameVariants = enumExtensionOptionName or packagePart
    private val enumValueExtensionOptionNameVariants = enumValueExtensionOptionName or packagePart
    private val serviceExtensionOptionNameVariants = serviceExtensionOptionName or packagePart
    private val methodExtensionOptionNameVariants = methodExtensionOptionName or packagePart

    val fieldTypeName = PsiElementFilter {
        it is ProtobufEnumDefinition || it is ProtobufMessageDefinition
    }

    val fieldTypeNameVariants = packagePart or fieldTypeName

    val rpcTypeName = PsiElementFilter {
        it is ProtobufMessageDefinition
    }

    val rpcTypeNameVariants = packagePart or rpcTypeName

    val extendTypeName = PsiElementFilter {
        it is ProtobufMessageDefinition
    }

    val extendTypeNameVariants = packagePart or extendTypeName

    private class TargetOptionFilter(private val option: Options) : PsiElementFilter {
        override fun isAccepted(element: PsiElement): Boolean {
            val extend = element.parentOfType<ProtobufExtendDefinition>() ?: return false
            val name = (extend.typeName?.resolve() as? ProtobufMessageDefinition)?.qualifiedName() ?: return false
            return name == option.qualifiedName
        }
    }
}
