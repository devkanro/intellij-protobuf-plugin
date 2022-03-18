package io.kanro.idea.plugin.protobuf.buf.ui

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.buf.schema.BufFieldSchema
import io.kanro.idea.plugin.protobuf.buf.util.elementSchema
import io.kanro.idea.plugin.protobuf.buf.util.renderDoc
import io.kanro.idea.plugin.protobuf.buf.util.yamlPath

class BufDocumentationProvider : DocumentationProvider {
    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        val path = originalElement?.yamlPath() ?: element?.yamlPath() ?: return null
        return path.toString()
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val schema = element?.elementSchema() ?: originalElement?.elementSchema() ?: return null

        return when (schema) {
            is BufFieldSchema -> return renderDoc(schema.document)
            else -> null
        }
    }
}
