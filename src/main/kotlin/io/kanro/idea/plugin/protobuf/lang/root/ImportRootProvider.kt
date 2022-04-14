package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import io.kanro.idea.plugin.protobuf.lang.settings.ProtobufSettings

class ImportRootProvider : ProtobufRootProvider {
    override fun roots(element: PsiElement): List<ProtobufRoot> {
        val settings = element.project.getService(ProtobufSettings::class.java)
        val fileUrl = element.containingFile.originalFile.virtualFile?.url

        return settings.state.importRoots.mapNotNull {
            val path = it.path ?: return@mapNotNull null
            val root = VirtualFileManager.getInstance().findFileByUrl(path) ?: return@mapNotNull null
            if (it.common) return@mapNotNull ProtobufRoot(null, root)
            if (fileUrl == null) return@mapNotNull null
            if (fileUrl.startsWith(path)) return@mapNotNull ProtobufRoot(null, root)
            null
        }
    }

    override fun searchScope(context: PsiElement): GlobalSearchScope? {
        return GlobalSearchScope.filesScope(context.project, roots(context).map { it.root })
    }

    override fun id(): String {
        return "settings"
    }
}
