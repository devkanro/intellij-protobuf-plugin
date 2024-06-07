package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.absolutely
import io.kanro.idea.plugin.protobuf.lang.psi.feature.LookupElement
import io.kanro.idea.plugin.protobuf.lang.psi.isExtension
import io.kanro.idea.plugin.protobuf.lang.psi.isFieldDefaultOption
import io.kanro.idea.plugin.protobuf.lang.psi.prev
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtensionRange
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOptionFieldName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.realItems
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver
import io.kanro.idea.plugin.protobuf.lang.support.Options

class ProtobufOptionFieldNameReference(name: ProtobufOptionFieldName) :
    PsiReferenceBase<ProtobufOptionFieldName>(name) {
    private fun optionType(): String? {
        val owner = element.parentOfType<ProtobufOptionOwner>() ?: return null
        return when (owner) {
            is ProtobufFile -> Options.FILE_OPTIONS.messageName
            is ProtobufMessageDefinition, is ProtobufGroupDefinition -> Options.MESSAGE_OPTIONS.messageName
            is ProtobufFieldDefinition, is ProtobufMapFieldDefinition -> Options.FIELD_OPTIONS.messageName
            is ProtobufOneofDefinition -> Options.ONEOF_OPTIONS.messageName
            is ProtobufEnumDefinition -> Options.ENUM_OPTIONS.messageName
            is ProtobufEnumValueDefinition -> Options.ENUM_VALUE_OPTIONS.messageName
            is ProtobufServiceDefinition -> Options.SERVICE_OPTIONS.messageName
            is ProtobufRpcDefinition -> Options.METHOD_OPTIONS.messageName
            is ProtobufExtensionRange -> Options.EXTENSION_RANGE_OPTIONS.messageName
            else -> null
        }
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return if (element.isExtension()) {
            element.extensionFieldName!!.textRangeInParent
        } else {
            TextRange.create(0, element.textLength)
        }
    }

    private fun resolveWithContext(
        scope: ProtobufScope,
        parentName: QualifiedName? = null,
    ): PsiElement? {
        val extension = element.extensionFieldName
        return if (extension != null) {
            if (extension.absolutely()) {
                ProtobufSymbolResolver.resolveAbsolutely(
                    element,
                    QualifiedName.fromComponents(extension.symbolNameList.map { it.text }),
                )
            } else {
                ProtobufSymbolResolver.resolveRelatively(
                    element,
                    QualifiedName.fromComponents(extension.symbolNameList.map { it.text }),
                )
            }
        } else {
            val name = parentName?.append(element.text) ?: QualifiedName.fromComponents(element.text)

            ProtobufSymbolResolver.resolveInScope(
                scope,
                name,
            )
        }
    }

    override fun resolve(): PsiElement? {
        val prevField = element.prev<ProtobufOptionFieldName>()

        if (prevField != null) {
            val field = prevField.reference?.resolve() as? ProtobufFieldLike ?: return null

            val scope =
                when (field) {
                    is ProtobufFieldDefinition -> (field.typeName.reference?.resolve() as? ProtobufMessageDefinition) ?: return null
                    is ProtobufGroupDefinition -> field
                    else -> return null
                }

            return resolveWithContext(scope)
        }

        if (element.isFieldDefaultOption()) {
            return element.parentOfType<ProtobufOptionOwner>() as? ProtobufFieldLike
        }

        return resolveWithContext(descriptor() ?: return null, optionType()?.let { QualifiedName.fromComponents(it) })
    }

    override fun getVariants(): Array<Any> {
        val type = optionType() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val descriptor = descriptor() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val message =
            ProtobufSymbolResolver.resolveInScope(
                descriptor,
                QualifiedName.fromComponents(type),
            ) as? ProtobufMessageDefinition ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val fields: MutableList<Any> =
            message.realItems().mapNotNull {
                if (it !is ProtobufFieldDefinition) return@mapNotNull null
                (it as? LookupElement)?.lookup()?.withInsertHandler(optionInsertHandler)
            }.toMutableList()
        if (Options.FIELD_OPTIONS.messageName == type) {
            fields +=
                LookupElementBuilder.create("default").withTypeText("option").withIcon(ProtobufIcons.FIELD)
                    .withInsertHandler(optionInsertHandler)
            fields +=
                LookupElementBuilder.create("json_name").withTypeText("option").withIcon(ProtobufIcons.FIELD)
                    .withInsertHandler(optionInsertHandler)
        }
        return fields.toTypedArray()
    }

    private fun descriptor(): ProtobufFile? {
        return ProtobufRootResolver.findFile("google/protobuf/descriptor.proto", element).firstOrNull()?.let {
            PsiManager.getInstance(element.project).findFile(it) as? ProtobufFile
        }
    }

    companion object {
        private val optionInsertHandler = SmartInsertHandler(" = ")
    }
}
