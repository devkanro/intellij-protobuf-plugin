package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtensionStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedStatement
import io.kanro.idea.plugin.protobuf.lang.psi.forEachPrev
import io.kanro.idea.plugin.protobuf.lang.psi.range
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType
import io.kanro.idea.plugin.protobuf.string.case.CommonWordSplitter
import io.kanro.idea.plugin.protobuf.string.case.SnakeCaseFormatter
import io.kanro.idea.plugin.protobuf.string.plural
import java.util.LinkedList

object FieldNameProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val element = parameters.position
        val field = element.parentOfType<ProtobufFieldDefinition>() ?: return
        val type = field.typeName.symbolNameList.lastOrNull()?.text ?: return
        val searchName = element.text.substringBeforeLast("_IntellijIdeaRulezzz", "")
        val prevNumber = prevFieldNumber(element)
        val inserter = fieldNumberInserter(prevNumber + 1)

        wellKnownTypeSuggestion(type).forEach {
            result.addAllElements(fieldNameSuggestion(searchName, it, field.fieldLabel?.text == "repeated", inserter))
        }
        result.restartCompletionOnPrefixChange(PlatformPatterns.string().endsWith("_"))
    }

    private fun fieldNameSuggestion(
        name: String,
        type: String,
        plural: Boolean,
        inserter: InsertHandler<LookupElement>
    ): List<LookupElement> {
        if (type.isEmpty()) return listOf()

        val result = mutableListOf<LookupElement>()
        val nameParts = CommonWordSplitter.split(name)
        val typeParts = LinkedList(CommonWordSplitter.split(type))
        if (plural) {
            typeParts[typeParts.lastIndex] = typeParts[typeParts.lastIndex].plural()
        }

        repeat(typeParts.size) {
            result += LookupElementBuilder.create(SnakeCaseFormatter.format(nameParts + typeParts))
                .withTypeText("field")
                .withInsertHandler(inserter)
            typeParts.removeFirst()
        }

        return result
    }

    private fun prevFieldNumber(context: PsiElement): Long {
        context.parentOfType<ProtobufFieldDefinition>()?.forEachPrev {
            when (it) {
                is ProtobufFieldDefinition -> return it.number() ?: 0
                is ProtobufReservedStatement -> return it.reservedRangeList.maxOf {
                    it.range()?.last?.takeIf { it != Long.MAX_VALUE } ?: 0
                }.takeIf { it != 0L } ?: return@forEachPrev
                is ProtobufExtensionStatement -> return it.reservedRangeList.maxOf {
                    it.range()?.last?.takeIf { it != Long.MAX_VALUE } ?: 0
                }.takeIf { it != 0L } ?: return@forEachPrev
            }
        }
        return 0
    }

    private fun fieldNumberInserter(number: Long): InsertHandler<LookupElement> {
        return SmartInsertHandler(" = $number;", -1)
    }

    private fun wellKnownTypeSuggestion(type: String): List<String> {
        return when (type) {
            "FieldMask" -> listOf("mask")
            "Timestamp" -> listOf("time")
            "Duration" -> listOf("duration", "offset")
            "Date" -> listOf("time")
            "Status" -> listOf("status", "error")
            else -> if (BuiltInType.isBuiltInType(type)) {
                listOf()
            } else {
                listOf(type)
            }
        }
    }
}
