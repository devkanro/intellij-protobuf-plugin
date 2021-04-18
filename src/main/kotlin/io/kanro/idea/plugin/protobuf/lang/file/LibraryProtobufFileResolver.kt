package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile

class LibraryProtobufFileResolver : RootsFileResolver() {
    override fun getRoots(project: Project): Iterable<VirtualFile> {
        return ProjectRootManager.getInstance(project).orderEntries().allLibrariesAndSdkClassesRoots.asIterable()
    }

    override fun getRoots(module: Module): Iterable<VirtualFile> {
        return ModuleRootManager.getInstance(module).orderEntries().allLibrariesAndSdkClassesRoots.asIterable()
    }
}
