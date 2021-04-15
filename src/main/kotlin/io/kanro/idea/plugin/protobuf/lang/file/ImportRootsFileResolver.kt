package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import io.kanro.idea.plugin.protobuf.lang.settings.ProtobufSettings

class ImportRootsFileResolver : RootsFileResolver() {
    private fun getImportRoots(project: Project): List<VirtualFile> {
        val settings = ServiceManager.getService(project, ProtobufSettings::class.java)
        return settings.state.importRoots.mapNotNull {
            VirtualFileManager.getInstance().findFileByUrl(it.path) ?: return@mapNotNull null
        }
    }

    override fun getRoots(project: Project): Iterable<VirtualFile> {
        return getImportRoots(project)
    }

    override fun getRoots(module: Module): Iterable<VirtualFile> {
        return getImportRoots(module.project)
    }
}
