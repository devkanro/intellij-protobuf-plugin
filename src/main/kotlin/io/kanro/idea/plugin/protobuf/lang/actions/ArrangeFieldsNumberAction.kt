package io.kanro.idea.plugin.protobuf.lang.actions

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.ProtobufFileType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValue
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumberScope
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufNumbered
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

abstract class ArrangeFieldsNumberActionHandler : EditorActionHandler() {
    private fun findItemsToSort(editor: Editor, caret: Caret, dataContext: DataContext?): List<ProtobufNumbered> {
        if (!editor.document.isWritable) return listOf()
        val file = dataContext?.getData(CommonDataKeys.PSI_FILE) ?: return listOf()
        if (file.fileType != ProtobufFileType.INSTANCE) return listOf()

        return if (caret.hasSelection()) {
            val start = file.findElementAt(caret.selectionStart) ?: return listOf()
            val end = file.findElementAt(caret.selectionEnd) ?: return listOf()

            val startScope = start.parentOfType<ProtobufNumberScope>() ?: return listOf()
            val endScope = end.parentOfType<ProtobufNumberScope>() ?: return listOf()

            if (startScope != endScope) return listOf()

            startScope.items().filterIsInstance<ProtobufNumbered>().filter {
                it.endOffset >= caret.selectionStart && it.startOffset <= caret.selectionEnd
            }
        } else {
            val element = file.findElementAt(caret.offset) ?: return listOf()
            val scope = element.parentOfType<ProtobufNumberScope>(true) ?: return listOf()
            scope.items().filterIsInstance<ProtobufNumbered>()
        }
    }

    override fun isEnabledForCaret(editor: Editor, caret: Caret, dataContext: DataContext?): Boolean {
        return findItemsToSort(editor, caret, dataContext).size > 1
    }

    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        val currentCaret = caret ?: editor.caretModel.currentCaret
        val items = findItemsToSort(editor, currentCaret, dataContext)
        if (items.isEmpty()) return

        doExecute(items, editor, currentCaret, dataContext)
    }

    abstract fun doExecute(items: List<ProtobufNumbered>, editor: Editor, caret: Caret, dataContext: DataContext?)
}

class ArrangeFieldsNumberToMaxHandler : ArrangeFieldsNumberActionHandler() {
    override fun doExecute(items: List<ProtobufNumbered>, editor: Editor, caret: Caret, dataContext: DataContext?) {
        val allowAlias = items.first().parentOfType<ProtobufNumberScope>()?.allowAlias() ?: false
        if (allowAlias) {
            val numbersNeed = items.mapNotNull {
                it.number()
            }.distinct().size + items.count { it.number() == null }.toLong()
            val maxInItems = items.maxOf { it.number() ?: -1 }.takeIf { it >= numbersNeed }

            var current = if (items.first() is ProtobufEnumValue) {
                maxInItems ?: (numbersNeed - 1)
            } else {
                maxInItems ?: numbersNeed
            }

            val mappedValue = mutableMapOf<String, String>()

            ApplicationManager.getApplication().runWriteAction {
                items.asReversed().forEach {
                    val integerLiteral = it.intValue()?.integerLiteral as? LeafElement ?: kotlin.run {
                        current--
                        return@forEach
                    }
                    val target = mappedValue.getOrPut(integerLiteral.text) {
                        (current--).toString()
                    }
                    integerLiteral.replaceWithText(target)
                }
            }
        } else {
            val numbersNeed = items.size.toLong()
            val maxInItems = items.maxOf { it.number() ?: -1 }.takeIf { it >= numbersNeed }
            var current = if (items.first() is ProtobufEnumValue) {
                maxInItems ?: (numbersNeed - 1)
            } else {
                maxInItems ?: numbersNeed
            }
            ApplicationManager.getApplication().runWriteAction {
                items.asReversed().forEach {
                    val integerLiteral = it.intValue()?.integerLiteral as? LeafElement ?: kotlin.run {
                        current--
                        return@forEach
                    }
                    val target = (current--).toString()
                    integerLiteral.replaceWithText(target)
                }
            }
        }
    }
}

class ArrangeFieldsNumberFromMinHandler : ArrangeFieldsNumberActionHandler() {
    override fun doExecute(items: List<ProtobufNumbered>, editor: Editor, caret: Caret, dataContext: DataContext?) {
        val allowAlias = items.first().parentOfType<ProtobufNumberScope>()?.allowAlias() ?: false
        val minInItems = items.minOf { it.number() ?: -1 }.takeIf { it >= 0 }

        var current = if (items.first() is ProtobufEnumValue) {
            minInItems ?: 0
        } else {
            minInItems ?: 1
        }

        if (allowAlias) {
            val mappedValue = mutableMapOf<String, String>()

            ApplicationManager.getApplication().runWriteAction {
                items.forEach {
                    val integerLiteral = it.intValue()?.integerLiteral as? LeafElement ?: kotlin.run {
                        current++
                        return@forEach
                    }
                    val target = mappedValue.getOrPut(integerLiteral.text) {
                        (current++).toString()
                    }
                    integerLiteral.replaceWithText(target)
                }
            }
        } else {
            ApplicationManager.getApplication().runWriteAction {
                items.forEach {
                    val integerLiteral = it.intValue()?.integerLiteral as? LeafElement ?: kotlin.run {
                        current++
                        return@forEach
                    }
                    val target = (current++).toString()
                    integerLiteral.replaceWithText(target)
                }
            }
        }
    }
}

class ArrangeFieldsNumberToMaxAction : EditorAction(ArrangeFieldsNumberToMaxHandler()) {
    override fun update(editor: Editor?, presentation: Presentation?, dataContext: DataContext?) {
        super.update(editor, presentation, dataContext)
        presentation?.apply {
            isVisible = isEnabled
        }
    }
}

class ArrangeFieldsNumberFromMinAction : EditorAction(ArrangeFieldsNumberFromMinHandler()) {
    override fun update(editor: Editor?, presentation: Presentation?, dataContext: DataContext?) {
        super.update(editor, presentation, dataContext)
        presentation?.apply {
            isVisible = isEnabled
        }
    }
}
