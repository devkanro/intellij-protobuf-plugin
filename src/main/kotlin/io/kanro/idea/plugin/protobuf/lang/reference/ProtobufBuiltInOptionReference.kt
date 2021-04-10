package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.file.FileResolver
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufBuiltInOptionName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufLookupItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.realItems
import io.kanro.idea.plugin.protobuf.lang.support.Options

class ProtobufBuiltInOptionReference(name: ProtobufBuiltInOptionName) :
    PsiReferenceBase<ProtobufBuiltInOptionName>(name) {
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
            else -> null
        }
    }

    override fun resolve(): PsiElement? {
        return ProtobufSymbolResolver.resolveInScope(
            descriptor() ?: return null,
            QualifiedName.fromComponents(optionType(), element.text)
        )
    }

    override fun getVariants(): Array<Any> {
        val type = optionType() ?: return arrayOf()
        val descriptor = descriptor() ?: return arrayOf()
        val message = ProtobufSymbolResolver.resolveInScope(
            descriptor,
            QualifiedName.fromComponents(type)
        ) as? ProtobufMessageDefinition ?: return arrayOf()
        val fields: MutableList<Any> = message.realItems().mapNotNull {
            if (it !is ProtobufFieldDefinition) return@mapNotNull null
            (it as? ProtobufLookupItem)?.lookup()
        }.toMutableList()
        if (Options.FIELD_OPTIONS.messageName == type) {
            fields += LookupElementBuilder.create("default")
                .withTypeText("option")
                .withIcon(Icons.FIELD)
        }
        return fields.toTypedArray()
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return element.identifierLiteral!!.textRangeInParent
    }

    private fun descriptor(): ProtobufFile? {
        return FileResolver.resolveFile("google/protobuf/descriptor.proto", element).firstOrNull()?.let {
            PsiManager.getInstance(element.project).findFile(it) as? ProtobufFile
        }
    }
}
