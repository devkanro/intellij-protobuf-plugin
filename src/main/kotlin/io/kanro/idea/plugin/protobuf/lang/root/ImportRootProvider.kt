package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.settings.ProtobufSettings

class ImportRootProvider : ProtobufRootProvider {
    override fun getProtobufRoots(element: PsiElement): List<ProtobufRoot> {
        val settings = element.project.getService(ProtobufSettings::class.java)
        val fileUrl = element.containingFile.originalFile.virtualFile?.url

        return settings.state.importRoots.mapNotNull {
            val root = VirtualFileManager.getInstance().findFileByUrl(it.path) ?: return@mapNotNull null
            if (it.common) return@mapNotNull ProtobufRoot(null, root)
            if (fileUrl == null) return@mapNotNull null
            if (fileUrl.startsWith(it.path)) return@mapNotNull ProtobufRoot(null, root)
            null
        }
    }

    override fun id(): String {
        return "settings"
    }

    override fun modificationTracker(context: PsiElement): ModificationTracker {
        return context.project.getService(ProtobufSettings::class.java)
    }
}
