package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile

class LibraryProtobufFileResolver : FileResolver {
    override fun findFile(path: String, project: Project): Iterable<VirtualFile> {
        return ProjectRootManager.getInstance(project).orderEntries().allSourceRoots.mapNotNull {
            it.findFileByRelativePath(path)?.takeIf { it.exists() }
        }
    }

    override fun findFile(path: String, module: Module): Iterable<VirtualFile> {
        return ModuleRootManager.getInstance(module).orderEntries()
            .withoutModuleSourceEntries().allSourceRoots.mapNotNull {
                it.findFileByRelativePath(path)?.takeIf { it.exists() }
            }
    }
}

class ModuleSourceProtobufFileResolver : FileResolver {
    override fun findFile(path: String, project: Project): Iterable<VirtualFile> {
        return listOf()
    }

    override fun findFile(path: String, module: Module): Iterable<VirtualFile> {
        return ModuleRootManager.getInstance(module).sourceRoots.mapNotNull {
            it.findFileByRelativePath(path)?.takeIf { it.exists() }
        }
    }
}
