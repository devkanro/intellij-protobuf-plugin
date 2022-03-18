package io.kanro.idea.plugin.protobuf.buf.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.PlatformIcons
import io.kanro.idea.plugin.protobuf.buf.schema.BufArraySchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumTypeSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufEnumValueSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufObjectSchema
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaScalarType
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchemaValueType
import io.kanro.idea.plugin.protobuf.buf.util.elementSchema
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import org.jetbrains.yaml.YAMLTokenTypes
import org.jetbrains.yaml.psi.YAMLPsiElement
import org.jetbrains.yaml.psi.YAMLValue

class BufYamlCompletionContributor : CompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        when (val definition = parameters.position.elementSchema()) {
            is BufFieldSchema -> addSchema(parameters.position, result, definition.valueType)
            is BufArraySchema -> addSchema(parameters.position, result, definition.itemType)
            is BufObjectSchema -> addSchema(parameters.position, result, definition)
            else -> {}
        }
    }

    private fun addSchema(
        context: PsiElement,
        result: CompletionResultSet,
        definition: BufSchemaValueType<out YAMLValue>
    ) {
        when (definition) {
            is BufEnumTypeSchema -> addSchema(context, result, definition)
            is BufObjectSchema -> addSchema(context, result, definition)
            BufSchemaScalarType.BOOL -> addBool(context, result)
            else -> {}
        }
    }

    private fun addSchema(context: PsiElement, result: CompletionResultSet, definition: BufObjectSchema) {
        result.addAllElements(definition.fields.map { lookupElement(context, it) })
    }

    private fun addSchema(context: PsiElement, result: CompletionResultSet, definition: BufEnumTypeSchema) {
        result.addAllElements(definition.values.map { lookupElement(context, it) })
    }

    private fun addBool(context: PsiElement, result: CompletionResultSet) {
        result.addAllElements(listOf(lookupElement(context, "true"), lookupElement(context, "false")))
    }

    private fun lookupElement(context: PsiElement, definition: BufEnumValueSchema): LookupElement {
        return LookupElementBuilder.create(definition.name)
            .withTypeText("enum value")
            .withIcon(PlatformIcons.ENUM_ICON)
    }

    private fun lookupElement(context: PsiElement, definition: BufFieldSchema): LookupElement {
        val suffix = when (definition.valueType) {
            is BufArraySchema -> smartIndentForField(context).takeIf { it.isNotEmpty() }?.let { "\n$it- " }
            is BufObjectSchema -> smartIndentForField(context).takeIf { it.isNotEmpty() }?.let { "\n$it" }
            BufSchemaScalarType.STRING -> "\"\""
            else -> ""
        }

        val offset = when (definition.valueType) {
            BufSchemaScalarType.STRING -> -1
            else -> 0
        }

        return LookupElementBuilder.create(definition.name)
            .withTypeText("field")
            .withIcon(PlatformIcons.FIELD_ICON)
            .withInsertHandler(SmartInsertHandler(": $suffix", offset, true))
    }

    private fun lookupElement(context: PsiElement, keyword: String): LookupElement {
        return LookupElementBuilder.create(keyword)
            .withTypeText("keyword")
    }

    private fun smartIndentForField(context: PsiElement): String {
        val yaml = context.parentOfType<YAMLPsiElement>()
        val prev = yaml?.prevSibling
        val prevIndent = prev?.takeIf { it.elementType == YAMLTokenTypes.INDENT }
        val prevEol = prevIndent?.prevSibling?.takeIf { it.elementType == YAMLTokenTypes.EOL }
            ?: prev?.takeIf { it.elementType == YAMLTokenTypes.EOL }

        if (prev != null && prevEol == null) return ""
        return (prevIndent?.text ?: "") + buildIndent(2)
    }

    private fun buildIndent(spaces: Int): String = buildString {
        for (i in 0 until spaces) {
            append(' ')
        }
    }
}
