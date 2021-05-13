package io.kanro.idea.plugin.protobuf.lang.formatter

import com.intellij.lang.ImportOptimizer
import com.intellij.psi.PsiFile
import io.kanro.idea.plugin.protobuf.lang.annotator.FileTracker
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufSyntaxStatement
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.resolve
import io.kanro.idea.plugin.protobuf.lang.psi.value
import io.kanro.idea.plugin.protobuf.lang.util.ProtobufPsiFactory

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
            val imports = file.imports()
            if (imports.count() == 0) return
            val tracker = FileTracker.tracker(file)
            val optimizedImports = imports.mapNotNull {
                val resolved = it.resolve() ?: return@mapNotNull it
                if (tracker.isUnused(resolved)) {
                    null
                } else {
                    it
                }
            }.sortedBy {
                it.stringValue?.value() ?: ""
            }.joinToString("\n") {
                it.text
            }

            imports.forEach {
                it.delete()
            }

            file.findChild<ProtobufPackageStatement>()?.let {
                val tempFile = ProtobufPsiFactory.createFile(file.project, "\n\n$optimizedImports")
                file.addRangeAfter(tempFile.firstChild, tempFile.lastChild, it)
                return
            }

            file.findChild<ProtobufSyntaxStatement>()?.let {
                val tempFile = ProtobufPsiFactory.createFile(file.project, "\n\n$optimizedImports")
                file.addRangeAfter(tempFile.firstChild, tempFile.lastChild, it)
                return
            }

            val tempFile = ProtobufPsiFactory.createFile(file.project, "$optimizedImports\n\n")
            file.addRangeBefore(tempFile.firstChild, tempFile.lastChild, file.firstChild)
            return
        }
    }
}
