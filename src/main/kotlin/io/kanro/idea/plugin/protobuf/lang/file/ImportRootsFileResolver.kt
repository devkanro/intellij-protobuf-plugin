package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import io.kanro.idea.plugin.protobuf.lang.settings.ProtobufSettings

class ImportRootsFileResolver : FileResolver {
    private fun getImportRoots(project: Project): List<VirtualFile> {
        val settings = ServiceManager.getService(project, ProtobufSettings::class.java)
        return settings.state.importRoots.mapNotNull {
            VirtualFileManager.getInstance().findFileByUrl(it.path) ?: return@mapNotNull null
        }
    }

    override fun findFile(path: String, project: Project): Iterable<VirtualFile> {
        return getImportRoots(project).mapNotNull {
            it.findFileByRelativePath(path)?.takeIf { it.exists() }
        }
    }

    override fun findFile(path: String, module: Module): Iterable<VirtualFile> {
        return findFile(path, module.project)
    }

    override fun collectProtobuf(path: String, project: Project): Iterable<VirtualFile> {
        val result = mutableListOf<VirtualFile>()
        getImportRoots(project).forEach {
            val directory = it.findFileByRelativePath(path) ?: return@forEach
            if (!directory.isDirectory) return@forEach
            FileResolver.collectProtobuf(directory, result)
        }
        return result
    }

    override fun collectProtobuf(path: String, module: Module): Iterable<VirtualFile> {
        return collectProtobuf(path, module.project)
    }
}
