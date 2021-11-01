package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.settings.ProtobufSettings

class ImportRootsFileResolver : RootsFileResolver() {
    private fun getImportRoots(project: Project, element: PsiElement): List<VirtualFile> {
        val settings = project.getService(ProtobufSettings::class.java)
        val fileUrl = element.containingFile.originalFile.virtualFile?.url

        return settings.state.importRoots.mapNotNull {
            val root = VirtualFileManager.getInstance().findFileByUrl(it.path) ?: return@mapNotNull null
            if (it.common) return@mapNotNull root
            if (fileUrl == null) return@mapNotNull null
            if (fileUrl.startsWith(it.path)) return@mapNotNull root
            null
        }
    }

    override fun getRoots(project: Project, element: PsiElement): Iterable<VirtualFile> {
        return getImportRoots(project, element)
    }

    override fun getRoots(module: Module, element: PsiElement): Iterable<VirtualFile> {
        return getImportRoots(module.project, element)
    }
}
