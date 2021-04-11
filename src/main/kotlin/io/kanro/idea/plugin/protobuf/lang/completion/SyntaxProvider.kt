package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.DeclarativeInsertHandler
import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext

class SyntaxProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        result.addElement(
            LookupElementBuilder.create("proto2")
                .withTypeText("syntax")
                .withInsertHandler(completeSyntaxInsert)
                .withAutoCompletionPolicy(AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE)
        )
        result.addElement(
            LookupElementBuilder.create("proto3")
                .withTypeText("syntax")
                .withInsertHandler(completeSyntaxInsert)
                .withAutoCompletionPolicy(AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE)
        )
    }

    companion object {
        private val completeSyntaxInsert = DeclarativeInsertHandler.Builder()
            .insertOrMove("\";")
            .build()
    }
}
