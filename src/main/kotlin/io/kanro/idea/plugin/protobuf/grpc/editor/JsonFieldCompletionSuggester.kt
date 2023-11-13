package io.kanro.idea.plugin.protobuf.grpc.editor

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import io.kanro.idea.plugin.protobuf.grpc.referece.GrpcReference

class JsonFieldCompletionSuggester : TypedHandlerDelegate() {
    override fun checkAutoPopup(
        charTyped: Char,
        project: Project,
        editor: Editor,
        file: PsiFile,
    ): Result {
        if (file !is JsonFile) return Result.CONTINUE
        if (charTyped != '"') return Result.CONTINUE
        // val request = file.injectedRequest() ?: return Result.CONTINUE
        // if(!request.isGrpcRequest()) return Result.CONTINUE
        ApplicationManager.getApplication().invokeLater {
            val element = file.findElementAt(editor.caretModel.offset) ?: return@invokeLater
            val parent = element.parent as? JsonStringLiteral ?: return@invokeLater
            if (parent.references.any { (it as? GrpcReference)?.variants?.isNotEmpty() == true }) {
                CodeCompletionHandlerBase.createHandler(CompletionType.BASIC).invokeCompletion(project, editor)
            }
        }
        return Result.CONTINUE
    }
}
