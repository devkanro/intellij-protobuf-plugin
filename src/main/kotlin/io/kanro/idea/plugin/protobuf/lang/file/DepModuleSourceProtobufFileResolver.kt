package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile

class DepModuleSourceProtobufFileResolver : RootsFileResolver() {
    override fun getRoots(project: Project): Iterable<VirtualFile> {
        return ProjectRootManager.getInstance(project).orderEntries()
            .withoutLibraries()
            .withoutSdk().allSourceRoots.asIterable()
    }

    override fun getRoots(module: Module): Iterable<VirtualFile> {
        return ModuleRootManager.getInstance(module).orderEntries()
            .withoutModuleSourceEntries()
            .withoutLibraries().withoutSdk().allSourceRoots.asIterable()
    }
}
