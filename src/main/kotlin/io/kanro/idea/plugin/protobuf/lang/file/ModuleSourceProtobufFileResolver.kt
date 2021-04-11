package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile

class ModuleSourceProtobufFileResolver : FileResolver {
    override fun findFile(path: String, project: Project): Iterable<VirtualFile> {
        return listOf()
    }

    override fun findFile(path: String, module: Module): Iterable<VirtualFile> {
        val rootManager = ModuleRootManager.getInstance(module)
        val root = rootManager.sourceRoots.takeIf { it.isNotEmpty() } ?: rootManager.contentRoots
        return root.mapNotNull {
            it.findFileByRelativePath(path)?.takeIf { it.exists() }
        }
    }

    override fun collectProtobuf(path: String, module: Module): Iterable<VirtualFile> {
        val result = mutableListOf<VirtualFile>()

        val rootManager = ModuleRootManager.getInstance(module)
        val root = rootManager.sourceRoots.takeIf { it.isNotEmpty() } ?: rootManager.contentRoots
        root.forEach {
            val directory = it.findFileByRelativePath(path) ?: return@forEach
            if (!directory.isDirectory) return@forEach
            FileResolver.collectProtobuf(directory, result)
        }
        return result
    }
}
