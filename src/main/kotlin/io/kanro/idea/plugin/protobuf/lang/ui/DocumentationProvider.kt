package io.kanro.idea.plugin.protobuf.lang.ui

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufDocumented

class ProtobufDocumentationProvider : DocumentationProvider {
    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        (originalElement as? ProtobufDocumented)?.navigateInfo()?.let { return it }
        (element as? ProtobufDocumented)?.navigateInfo()?.let { return it }
        return null
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        (originalElement as? ProtobufDocumented)?.document()?.let { return it }
        (element as? ProtobufDocumented)?.document()?.let { return it }
        return null
    }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? {
        (originalElement as? ProtobufDocumented)?.hoverDocument()?.let { return it }
        (element as? ProtobufDocumented)?.hoverDocument()?.let { return it }
        return null
    }
}
