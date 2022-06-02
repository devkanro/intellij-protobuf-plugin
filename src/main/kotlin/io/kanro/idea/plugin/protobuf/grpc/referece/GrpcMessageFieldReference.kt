package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.grpc.grpcMethod
import io.kanro.idea.plugin.protobuf.grpc.injectedRequest
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.filterItem
import io.kanro.idea.plugin.protobuf.lang.psi.firstItemOrNull
import io.kanro.idea.plugin.protobuf.lang.psi.jsonName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.repeated
import io.kanro.idea.plugin.protobuf.lang.psi.resolveField
import io.kanro.idea.plugin.protobuf.lang.psi.resolveFieldType

class GrpcMessageFieldReference(field: JsonStringLiteral) : PsiReferenceBase<JsonStringLiteral>(field) {
    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(1, element.textLength - 1)
    }

    override fun resolve(): PsiElement? {
        val property = element.parent as? JsonProperty ?: return null
        return property.resolve()
    }

    override fun getVariants(): Array<Any> {
        val property = element.parent as? JsonProperty ?: return arrayOf()
        val type = property.resolveParentType() ?: return arrayOf()

        return when (type) {
            is ProtobufMessageDefinition -> {
                type.filterItem<ProtobufFieldLike> { true }.mapNotNull { lookupFor(it) }.toTypedArray()
            }
            is ProtobufGroupDefinition -> {
                type.filterItem<ProtobufFieldLike> { true }.mapNotNull { lookupFor(it) }.toTypedArray()
            }
            else -> arrayOf()
        }
    }

    private fun lookupFor(element: ProtobufFieldLike): LookupElementBuilder? {
        val jsonName = element.jsonName() ?: return null

        return element.lookup(jsonName)?.withPresentableText(jsonName)?.let {
            if (element.name() != jsonName) {
                it.appendTailText(" (${element.name()})", true)
            } else {
                it
            }
        }?.withInsertHandler(JsonFieldSmartInsertHandler(element))
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return element
    }
}

class GrpcMessageEnumValueReference(value: JsonStringLiteral) : PsiReferenceBase<JsonStringLiteral>(value) {
    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(1, element.textLength - 1)
    }

    override fun resolve(): PsiElement? {
        val property = element.parent as? JsonProperty ?: return null
        val field = property.resolve() as? ProtobufFieldDefinition ?: return null
        val enum = field.typeName.reference?.resolve() as? ProtobufEnumDefinition ?: return null
        return enum.firstItemOrNull<ProtobufEnumValueDefinition> { it.name() == element.value }
    }

    override fun getVariants(): Array<Any> {
        val property = element.parent as? JsonProperty ?: return arrayOf()
        val field = property.resolve() as? ProtobufFieldDefinition ?: return arrayOf()
        val enum = field.typeName.reference?.resolve() as? ProtobufEnumDefinition ?: return arrayOf()

        return enum.filterItem<ProtobufEnumValueDefinition> { true }.mapNotNull {
            it.lookup()?.withInsertHandler(SmartInsertHandler("\","))
        }.toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return element
    }
}

private class JsonFieldSmartInsertHandler(private val element: ProtobufFieldLike) : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val insertHandler = when (element) {
            is ProtobufFieldDefinition -> {
                if (element.repeated()) {
                    SmartInsertHandler("\": []", -1)
                } else {
                    when (element.typeName.reference?.resolve()) {
                        is ProtobufMessageDefinition -> SmartInsertHandler("\": {}", -1)
                        is ProtobufEnumDefinition -> SmartInsertHandler("\": \"\"", -1, true)
                        else -> {
                            when (element.typeName.text) {
                                "string", "bytes" -> SmartInsertHandler("\": \"\"", -1)
                                "bool" -> SmartInsertHandler("\": ", 0, true)
                                else -> SmartInsertHandler("\": ")
                            }
                        }
                    }
                }
            }
            is ProtobufGroupDefinition -> {
                if (element.repeated()) {
                    SmartInsertHandler("\": []", -1)
                } else {
                    SmartInsertHandler("\": {}", -1)
                }
            }
            else -> SmartInsertHandler("\": {}", -1)
        }
        insertHandler.handleInsert(context, item)
    }
}

private fun JsonProperty.qualifiedName(): QualifiedName? {
    return CachedValuesManager.getCachedValue(this) {
        var e = this
        val q = mutableListOf<String>()
        while (true) {
            q += e.name
            e = e.parentOfType() ?: break
        }
        q.reverse()

        CachedValueProvider.Result.create(
            QualifiedName.fromComponents(q), PsiModificationTracker.MODIFICATION_COUNT
        )
    }
}

private fun JsonProperty.resolveParentType(): PsiElement? {
    return CachedValuesManager.getCachedValue(this) {
        val request = injectedRequest() ?: return@getCachedValue null
        val rpcDefinition = request.grpcMethod() ?: return@getCachedValue null
        val input = rpcDefinition.rpcIOList.firstOrNull()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition
            ?: return@getCachedValue null
        val qualifiedName = qualifiedName() ?: return@getCachedValue null
        CachedValueProvider.Result.create(
            input.resolveFieldType(qualifiedName.removeTail(1)), PsiModificationTracker.MODIFICATION_COUNT
        )
    }
}

private fun JsonProperty.resolve(): PsiElement? {
    return CachedValuesManager.getCachedValue(this) {
        val request = injectedRequest() ?: return@getCachedValue null
        val rpcDefinition = request.grpcMethod() ?: return@getCachedValue null
        val input = rpcDefinition.rpcIOList.firstOrNull()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition
            ?: return@getCachedValue null
        val qualifiedName = qualifiedName() ?: return@getCachedValue null

        CachedValueProvider.Result.create(
            input.resolveField(qualifiedName), PsiModificationTracker.MODIFICATION_COUNT
        )
    }
}
