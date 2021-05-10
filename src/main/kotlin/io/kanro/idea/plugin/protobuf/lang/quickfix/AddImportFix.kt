package io.kanro.idea.plugin.protobuf.lang.quickfix

import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.hint.QuestionAction
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.codeInspection.HintAction
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ListPopupStep
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.StubIndex
import com.intellij.ui.popup.list.ListPopupImpl
import io.kanro.idea.plugin.protobuf.lang.file.FileResolver
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.absolutely
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.QualifiedNameIndex
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.ShortNameIndex
import io.kanro.idea.plugin.protobuf.lang.util.ProtobufPsiFactory
import io.kanro.idea.plugin.protobuf.lang.util.removeCommonPrefix
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.ListCellRenderer

class AddImportFix(private val typeName: ProtobufTypeName) :
    BaseIntentionAction(),
    HintAction,
    HighPriorityAction {
    private lateinit var elements: Array<ProtobufDefinition>

    override fun getFamilyName(): String {
        return "Import"
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        if (!typeName.isValid) return false
        val parts = typeName.symbolNameList
        val name = parts.joinToString(".") { it.text }
        if (typeName.absolutely() || parts.size > 1) {
            this.elements = StubIndex.getElements(
                QualifiedNameIndex.key, name,
                project, FileResolver.searchScope(typeName),
                ProtobufElement::class.java
            ).filterIsInstance<ProtobufDefinition>().toTypedArray()
        } else {
            this.elements = StubIndex.getElements(
                ShortNameIndex.key, name,
                project, FileResolver.searchScope(typeName),
                ProtobufElement::class.java
            ).filterIsInstance<ProtobufDefinition>().toTypedArray()
        }

        return elements.isNotEmpty()
    }

    override fun invoke(project: Project, editor: Editor, file: PsiFile?) {
        CommandProcessor.getInstance().runUndoTransparentAction {
            createAction(project, editor, typeName, elements).execute()
        }
    }

    private fun createAction(
        project: Project,
        editor: Editor,
        typeName: ProtobufTypeName,
        elements: Array<ProtobufDefinition>
    ): ProtobufAddImportAction {
        return ProtobufAddImportAction(project, editor, typeName, elements)
    }

    override fun getText(): String {
        return if (elements.size == 1) {
            "Import from \"${elements[0].importPath(typeName.file())}\""
        } else {
            "Import from \"${elements[0].importPath(typeName.file())}\" or other ${elements.size - 1} files"
        }
    }

    override fun showHint(editor: Editor): Boolean {
        return createAction(editor.project ?: return false, editor, typeName, elements).showHint()
    }
}

class ProtobufAddImportAction(
    private val project: Project,
    private val editor: Editor,
    private val typeName: ProtobufTypeName,
    private val elements: Array<ProtobufDefinition>
) : QuestionAction {
    private fun hintText(): String {
        return if (elements.size == 1) {
            "${elements[0].qualifiedName()}? Alt+Enter"
        } else {
            "${elements[0].qualifiedName()}? (multiple choices...) Alt+Enter"
        }
    }

    fun showHint(): Boolean {
        val range = typeName.textRange
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
                                cellHasFocus
                            )
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

            override fun onChosen(selectedValue: ProtobufDefinition?, finalChoice: Boolean): PopupStep<String>? {
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
                    val file = typeName.file()
                    val parts = typeName.symbolNameList
                    val targetName = name.removeCommonPrefix(file.scope()).toString()

                    file.addImport(element)
                    if (!typeName.absolutely() && parts.size == 1) {
                        if (!parts.first().textMatches(targetName)) {
                            typeName.replace(ProtobufPsiFactory.createTypeName(project, targetName))
                        }
                    }
                    PsiDocumentManager.getInstance(project).commitAllDocuments()
                }
            },
            "Import ${element.importPath(typeName.file())}", null
        )
        return true
    }
}
