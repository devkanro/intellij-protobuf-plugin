package io.kanro.idea.plugin.protobuf.lang.ui

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufQuickNavigateInfo

class ProtobufDocumentationProvider : DocumentationProvider {
    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        (originalElement as? ProtobufQuickNavigateInfo)?.navigateInfo()?.let { return it }
        (element as? ProtobufQuickNavigateInfo)?.navigateInfo()?.let { return it }
        return null
    }
}
