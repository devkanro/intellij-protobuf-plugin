package io.kanro.idea.plugin.protobuf.lang.quickfix

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.refactoring.RefactoringFactory
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition

class RenameFix(private val newName: String, private val element: ProtobufDefinition?) : BaseIntentionAction() {
    init {
        text = "Rename to \"$newName\""
    }

    override fun getFamilyName(): String {
        return "Rename"
    }

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return file is ProtobufFile && file.isWritable
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is ProtobufFile) return
        element ?: return
        DumbService.getInstance(project).smartInvokeLater {
            RefactoringFactory.getInstance(project).createRename(element, newName).run()
        }
    }
}
