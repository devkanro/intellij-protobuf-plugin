package io.kanro.idea.plugin.protobuf.lang.formatter

import com.intellij.lang.ImportOptimizer
import com.intellij.psi.PsiFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile

class ProtobufImportOptimizer : ImportOptimizer {
    override fun supports(file: PsiFile): Boolean {
        return file is ProtobufFile
    }

    override fun processFile(file: PsiFile): Runnable {
        return Runnable {}
    }
}
