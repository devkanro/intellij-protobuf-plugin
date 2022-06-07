package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.ArrayUtilRt
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.filterItem
import io.kanro.idea.plugin.protobuf.lang.psi.firstItemOrNull

class GrpcMessageEnumValueReference(value: JsonStringLiteral) : PsiReferenceBase<JsonStringLiteral>(value),
    GrpcReference {
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
        val property = element.parent as? JsonProperty ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val field = property.resolve() as? ProtobufFieldDefinition ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        val enum =
            field.typeName.reference?.resolve() as? ProtobufEnumDefinition ?: return ArrayUtilRt.EMPTY_OBJECT_ARRAY

        return enum.filterItem<ProtobufEnumValueDefinition> { true }.mapNotNull {
            it.lookup()?.withInsertHandler(SmartInsertHandler("\","))
        }.toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return element
    }
}

