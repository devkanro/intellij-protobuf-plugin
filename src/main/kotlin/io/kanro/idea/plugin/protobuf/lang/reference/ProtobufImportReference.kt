package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import io.kanro.idea.plugin.protobuf.lang.file.FileResolver
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement

class ProtobufImportReference(import: ProtobufImportStatement) : PsiReferenceBase<ProtobufImportStatement>(import) {
    override fun resolve(): PsiElement? {
        return resolve(element)
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        return element.stringValue!!.textRangeInParent
    }

    companion object {
        fun resolve(element: ProtobufImportStatement): PsiElement? {
            val filePath = element.stringValue!!.text
            val file =
                FileResolver.resolveFile(filePath.substring(1, filePath.length - 1), element).firstOrNull()
                    ?: return null
            return PsiManager.getInstance(element.project).findFile(file)
        }
    }
}
