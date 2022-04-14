package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope

interface ProtobufRootProvider {
    companion object {
        var extensionPoint: ExtensionPointName<ProtobufRootProvider> =
            ExtensionPointName.create("io.kanro.idea.plugin.protobuf.rootProvider")
    }

    fun id(): String?

    fun roots(context: PsiElement): List<ProtobufRoot>

    fun searchScope(context: PsiElement): GlobalSearchScope? {
        return GlobalSearchScope.filesScope(context.project, roots(context).map { it.root })
    }
}
