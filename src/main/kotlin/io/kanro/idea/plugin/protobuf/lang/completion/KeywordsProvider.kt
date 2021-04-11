package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext

class KeywordsProvider(keywords: List<String>) : CompletionProvider<CompletionParameters>() {
    private val keywordElements = keywords.map { keywordElement(it) }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        result.addAllElements(keywordElements)
    }

    companion object {
        val topLevelKeywords
            get() = KeywordsProvider(
                listOf(
                    "syntax", "package", "import", "option", "message", "enum", "service", "extend"
                )
            )

        val messageLevelKeywords
            get() = KeywordsProvider(
                listOf(
                    "option", "message", "enum", "extend", "oneof", "group", "extensions", "reserved",
                    "repeated", "optional", "required"
                )
            )

        val enumLevelKeywords
            get() = KeywordsProvider(
                listOf(
                    "option", "reserved"
                )
            )

        val serviceLevelKeywords
            get() = KeywordsProvider(
                listOf(
                    "option", "rpc"
                )
            )
    }
}
