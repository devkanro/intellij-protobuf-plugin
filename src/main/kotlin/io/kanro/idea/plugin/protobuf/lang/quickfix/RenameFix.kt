package io.kanro.idea.plugin.protobuf.lang.quickfix

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.RefactoringFactory
import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition

class RenameFix(private val newName: String) : PsiElementBaseIntentionAction() {
    init {
        text = "Rename to \"$newName\""
    }

    override fun getFamilyName(): String {
        return "Rename"
    }

    override fun isAvailable(
        project: Project,
        editor: Editor?,
        element: PsiElement,
    ): Boolean {
        return element.parentOfType<ProtobufDefinition>() != null
    }

    override fun invoke(
        project: Project,
        editor: Editor?,
        element: PsiElement,
    ) {
        DumbService.getInstance(project).smartInvokeLater {
            val namedElement = element.parentOfType<NamedElement>(true) ?: return@smartInvokeLater
            RefactoringFactory.getInstance(project).createRename(namedElement, newName).run()
        }
    }
}
