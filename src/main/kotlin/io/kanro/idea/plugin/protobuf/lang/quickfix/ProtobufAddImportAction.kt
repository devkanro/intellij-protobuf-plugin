package io.kanro.idea.plugin.protobuf.lang.quickfix

import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.hint.QuestionAction
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ListPopupStep
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.parentOfType
import com.intellij.ui.popup.list.ListPopupImpl
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ReferenceElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.util.removeCommonPrefix
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.ListCellRenderer

class ProtobufAddImportAction(
    private val project: Project,
    private val editor: Editor,
    private val host: ReferenceElement,
    private val elements: Array<ProtobufDefinition>,
) : QuestionAction {
    private fun hintText(): String {
        return if (elements.size == 1) {
            "${elements[0].qualifiedName()}? Alt+Enter"
        } else {
            "${elements[0].qualifiedName()}? (multiple choices...) Alt+Enter"
        }
    }

    fun showHint(): Boolean {
        val range = host.textRange
        HintManager.getInstance()
            .showQuestionHint(editor, hintText(), range.startOffset, range.endOffset, this)
        return true
    }

    override fun execute(): Boolean {
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        if (elements.isEmpty()) return false
        if (elements.size == 1) {
            addImport(elements.first())
            return true
        }

        object : ListPopupImpl(project, createListStep(elements)) {
            private val psiRenderer = DefaultPsiElementCellRenderer()

            override fun getListElementRenderer(): ListCellRenderer<ProtobufDefinition> {
                return ListCellRenderer { list, value, index, isSelected, cellHasFocus ->
                    JPanel(BorderLayout()).apply {
                        add(
                            psiRenderer.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus,
                            ),
                        )
                    }
                }
            }
        }.showInBestPositionFor(editor)
        return true
    }

    private fun createListStep(elements: Array<ProtobufDefinition>): ListPopupStep<ProtobufDefinition> {
        return object : BaseListPopupStep<ProtobufDefinition>("Imports", elements.toMutableList()) {
            override fun isAutoSelectionEnabled() = false

            override fun isSpeedSearchEnabled() = true

            override fun onChosen(
                selectedValue: ProtobufDefinition?,
                finalChoice: Boolean,
            ): PopupStep<String>? {
                if (selectedValue == null || project.isDisposed) return null
                if (finalChoice) {
                    addImport(selectedValue)
                    return null
                }
                return null
            }

            override fun hasSubstep(selectedValue: ProtobufDefinition?) = false

            override fun getTextFor(value: ProtobufDefinition) = value.qualifiedName().toString()

            override fun getIconFor(value: ProtobufDefinition) = value.getIcon(false)
        }
    }

    private fun addImport(element: ProtobufDefinition): Boolean {
        val name = element.qualifiedName() ?: return false
        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        psiDocumentManager.commitAllDocuments()
        CommandProcessor.getInstance().executeCommand(
            project,
            {
                ApplicationManager.getApplication().runWriteAction {
                    val currentScope = host.parentOfType<ProtobufScope>() ?: return@runWriteAction
                    val file = currentScope.file()
                    val targetName = name.removeCommonPrefix(currentScope.scope())

                    file.addImport(element)

                    val symbol = host.symbol()
                    if (symbol != null) {
                        val parts = symbol.components
                        if (parts.size == 1) {
                            host.rename(targetName)
                        }
                    }

                    PsiDocumentManager.getInstance(project).commitAllDocuments()
                }
            },
            "Import ${element.importPath(host.containingFile.originalFile as ProtobufFile)}",
            null,
        )
        return true
    }
}
