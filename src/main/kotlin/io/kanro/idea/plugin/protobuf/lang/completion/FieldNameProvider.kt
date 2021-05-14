package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
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

        result.addAllElements(fieldNameSuggestion(searchName, type, field.fieldLabel?.text == "repeated"))
        result.restartCompletionOnPrefixChange(PlatformPatterns.string().endsWith("_"))
    }

    private fun fieldNameSuggestion(name: String, type: String, plural: Boolean): List<LookupElement> {
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
            typeParts.removeFirst()
        }

        return result
    }
}
