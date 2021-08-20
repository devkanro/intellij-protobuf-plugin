package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
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

        val methodLevelKeywords
            get() = KeywordsProvider(
                listOf(
                    "option"
                )
            )

        val rpcLevelKeywords
            get() = KeywordsProvider(
                listOf(
                    "returns"
                )
            )

        fun keywordElement(keyword: String): LookupElement {
            return LookupElementBuilder.create(keyword).withTypeText("keyword")
                .withInsertHandler(keywordInsertHandler(keyword))
        }

        private val insertHandlerCache = mutableMapOf<String, InsertHandler<LookupElement>>()

        fun keywordInsertHandler(keyword: String): InsertHandler<LookupElement> {
            return insertHandlerCache.getOrPut(keyword) {
                when (keyword) {
                    "import" -> SmartInsertHandler(" \"\"", -1, true)
                    "syntax" -> SmartInsertHandler(" = \"\"", -1, true)
                    "returns" -> SmartInsertHandler(" ();", -2, true)
                    else -> SmartInsertHandler(" ")
                }
            }
        }
    }
}
