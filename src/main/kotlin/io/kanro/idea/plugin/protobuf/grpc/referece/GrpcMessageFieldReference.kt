package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.filterItem
import io.kanro.idea.plugin.protobuf.lang.psi.jsonName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.repeated
import kotlin.math.max

class GrpcMessageFieldReference(field: JsonStringLiteral) : PsiReferenceBase<JsonStringLiteral>(field), GrpcReference {
    override fun calculateDefaultRangeInElement(): TextRange {
        return TextRange.create(1, max(element.textLength - 1, 1))
    }

    override fun resolve(): PsiElement? {
        val property = element.parent as? JsonProperty ?: return null
        return property.resolve()
    }

    override fun getVariants(): Array<Any> {
        val property = element.parent as? JsonProperty ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val type = property.resolveParentType() ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val obj = property.parent as? JsonObject ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val existsFields = obj.propertyList.map { it.name }.toSet()

        return when (type) {
            is ProtobufMessageDefinition -> {
                type.filterItem<ProtobufFieldLike> { true }
                    .mapNotNull { lookupFor(it, existsFields) }.toTypedArray()
            }
            is ProtobufGroupDefinition -> {
                type.filterItem<ProtobufFieldLike> { true }
                    .mapNotNull { lookupFor(it, existsFields) }.toTypedArray()
            }
            else -> ArrayUtilRt.EMPTY_OBJECT_ARRAY
        }
    }

    private fun lookupFor(element: ProtobufFieldLike, existsFields: Set<String>): LookupElementBuilder? {
        val jsonName = element.jsonName() ?: return null
        if (jsonName in existsFields) return null
        if (element.name() in existsFields) return null

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
