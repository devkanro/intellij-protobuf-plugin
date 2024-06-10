package io.kanro.idea.plugin.protobuf.grpc.referece

import com.bybutter.sisyphus.protobuf.primitives.Timestamp
import com.bybutter.sisyphus.protobuf.primitives.now
import com.bybutter.sisyphus.protobuf.primitives.string
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
import io.kanro.idea.plugin.protobuf.lang.psi.filterItem
import io.kanro.idea.plugin.protobuf.lang.psi.firstItemOrNull
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.repeated
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.support.WellknownTypes
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

        if (type is ProtobufMessageDefinition) {
            val message = type.qualifiedName().toString()
            if (message == WellknownTypes.ANY) {
                val field =
                    type.firstItemOrNull<ProtobufFieldLike> { it.name() == "type_url" }
                        ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
                return listOfNotNull(lookupForTypeUrl(field)).toTypedArray()
            } else if (message in WellknownTypes.types) {
                return ArrayUtilRt.EMPTY_OBJECT_ARRAY
            }
        }

        return when (type) {
            is ProtobufMessageDefinition -> {
                type.filterItem<ProtobufFieldLike> { true }.mapNotNull { lookupFor(it, existsFields) }.toTypedArray()
            }

            is ProtobufGroupDefinition -> {
                type.filterItem<ProtobufFieldLike> { true }.mapNotNull { lookupFor(it, existsFields) }.toTypedArray()
            }

            else -> ArrayUtilRt.EMPTY_OBJECT_ARRAY
        }
    }

    private fun lookupFor(
        element: ProtobufFieldLike,
        existsFields: Set<String>,
    ): LookupElementBuilder? {
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

    private fun lookupForTypeUrl(element: ProtobufFieldLike): LookupElementBuilder? {
        return element.lookup("@type")?.withPresentableText("@type")?.withLookupString("@type")
            ?.appendTailText(" (type_url)", true)?.withInsertHandler(SmartInsertHandler("\": \"\"", -1, true))
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return element
    }
}

private class JsonFieldSmartInsertHandler(private val element: ProtobufFieldLike) : InsertHandler<LookupElement> {
    companion object {
        private val OBJECT_INSERT_HANDLER = SmartInsertHandler("\": {}", -1)
        private val LIST_INSERT_HANDLER = SmartInsertHandler("\": []", -1)
        private val BOOLEAN_INSERT_HANDLER = SmartInsertHandler("\": ", 0, true)
        private val STRING_INSERT_HANDLER = SmartInsertHandler("\": \"\"", -1)
        private val STRING_REF_INSERT_HANDLER = SmartInsertHandler("\": \"\"", -1, true)
        private val NUMBER_INSERT_HANDLER = SmartInsertHandler("\": ")
    }

    override fun handleInsert(
        context: InsertionContext,
        item: LookupElement,
    ) {
        val insertHandler =
            when (element) {
                is ProtobufFieldDefinition -> {
                    if (element.repeated()) {
                        LIST_INSERT_HANDLER
                    } else {
                        when (val type = element.typeName.resolve()) {
                            is ProtobufMessageDefinition -> {
                                val name = type.qualifiedName().toString()
                                if (name in WellknownTypes.types) {
                                    when (name) {
                                        WellknownTypes.ANY, WellknownTypes.STRUCT -> OBJECT_INSERT_HANDLER
                                        WellknownTypes.LIST_VALUE -> LIST_INSERT_HANDLER
                                        WellknownTypes.BOOL_VALUE -> BOOLEAN_INSERT_HANDLER
                                        WellknownTypes.TIMESTAMP ->
                                            SmartInsertHandler(
                                                "\": \"${
                                                    Timestamp.now().string()
                                                }\"",
                                                -1,
                                            )

                                        WellknownTypes.DURATION -> SmartInsertHandler("\": \"1.0s\"", -1)
                                        WellknownTypes.FIELD_MASK,
                                        WellknownTypes.STRING_VALUE,
                                        WellknownTypes.BYTES_VALUE,
                                        -> STRING_INSERT_HANDLER

                                        else -> NUMBER_INSERT_HANDLER
                                    }
                                } else {
                                    OBJECT_INSERT_HANDLER
                                }
                            }

                            is ProtobufEnumDefinition -> STRING_REF_INSERT_HANDLER
                            else -> {
                                when (element.typeName.text) {
                                    "string", "bytes" -> STRING_INSERT_HANDLER
                                    "bool" -> BOOLEAN_INSERT_HANDLER
                                    else -> NUMBER_INSERT_HANDLER
                                }
                            }
                        }
                    }
                }

                is ProtobufGroupDefinition -> {
                    if (element.repeated()) {
                        LIST_INSERT_HANDLER
                    } else {
                        OBJECT_INSERT_HANDLER
                    }
                }

                else -> OBJECT_INSERT_HANDLER
            }
        insertHandler.handleInsert(context, item)
    }
}
