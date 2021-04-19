package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement

class ComposedInsertHandler(private vararg val handlers: InsertHandler<LookupElement>) : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        handlers.forEach {
            it.handleInsert(context, item)
        }
    }
}
