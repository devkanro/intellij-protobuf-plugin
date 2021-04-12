package io.kanro.idea.plugin.protobuf.lang.formatter

import com.intellij.lang.ImportOptimizer
import com.intellij.psi.PsiFile
import io.kanro.idea.plugin.protobuf.lang.annotator.ImportTracker
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.resolve

class ProtobufImportOptimizer : ImportOptimizer {
    override fun supports(file: PsiFile): Boolean {
        return file is ProtobufFile
    }

    override fun processFile(file: PsiFile): Runnable {
        return Runnable {
            ProtobufImportOptimizer.processFile(file)
        }
    }

    companion object {
        fun processFile(file: PsiFile) {
            if (file !is ProtobufFile) return
            val tracker = ImportTracker.tracker(file)
            file.imports().forEach {
                val resolved = it.resolve() ?: return@forEach
                if (tracker.isUnused(resolved)) {
                    it.delete()
                }
            }
        }
    }
}
