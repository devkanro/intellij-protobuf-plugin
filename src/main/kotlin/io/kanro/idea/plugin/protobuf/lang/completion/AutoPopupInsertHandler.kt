package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement

object AutoPopupInsertHandler : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val editor = context.editor
        val project = editor.project ?: return
        AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null)
    }
}
