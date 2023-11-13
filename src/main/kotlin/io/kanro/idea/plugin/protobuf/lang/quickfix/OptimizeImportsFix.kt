package io.kanro.idea.plugin.protobuf.lang.quickfix

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import io.kanro.idea.plugin.protobuf.lang.formatter.ProtobufImportOptimizer
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile

class OptimizeImportsFix : BaseIntentionAction() {
    init {
        text = "Remove unused imports"
    }

    override fun getFamilyName(): String {
        return "Cleanup"
    }

    override fun isAvailable(
        project: Project,
        editor: Editor?,
        file: PsiFile?,
    ): Boolean {
        return file is ProtobufFile && file.isWritable
    }

    override fun invoke(
        project: Project,
        editor: Editor?,
        file: PsiFile?,
    ) {
        if (file !is ProtobufFile) return
        ProtobufImportOptimizer.processFile(file)
    }
}
