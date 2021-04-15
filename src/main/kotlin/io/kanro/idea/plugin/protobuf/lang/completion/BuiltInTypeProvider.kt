package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.AddSpaceInsertHandler
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import io.kanro.idea.plugin.protobuf.lang.support.BuiltInType

class BuiltInTypeProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        result.addAllElements(builtInTypes)
    }

    companion object {
        private val builtInTypes = BuiltInType.values().map {
            builtInTypeElement(it.value())
        }

        fun builtInTypeElement(keyword: String): LookupElement {
            return LookupElementBuilder.create(keyword).withTypeText("built-in")
                .withInsertHandler(AddSpaceInsertHandler.INSTANCE)
        }
    }
}
