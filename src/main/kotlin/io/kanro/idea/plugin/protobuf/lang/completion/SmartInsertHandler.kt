package io.kanro.idea.plugin.protobuf.lang.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import kotlin.math.min

class SmartInsertHandler(val value: String, val offset: Int = 0, val autoPopup: Boolean = false) :
    InsertHandler<LookupElement> {
    override fun handleInsert(
        context: InsertionContext,
        item: LookupElement,
    ) {
        val editor = context.editor
        val project = editor.project ?: return
        val model = editor.caretModel

        val moveOffset = getMoveOffset(editor)
        model.moveToOffset(model.offset + moveOffset)
        EditorModificationUtil.insertStringAtCaret(editor, value.substring(moveOffset))
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        model.moveToOffset(model.offset + offset)

        if (autoPopup) {
            AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null)
        }
    }

    private fun getMoveOffset(editor: Editor): Int {
        val document = editor.document
        val startOffset = editor.caretModel.offset
        val endOffset = min(document.textLength, startOffset + value.length)
        val tailText = document.getText(TextRange.create(startOffset, endOffset))
        val alreadyText = tailText.commonPrefixWith(value)
        return alreadyText.length
    }
}
