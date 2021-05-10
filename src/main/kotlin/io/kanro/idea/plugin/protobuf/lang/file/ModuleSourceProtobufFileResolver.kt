package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope

class ModuleSourceProtobufFileResolver : RootsFileResolver() {
    override fun searchScope(project: Project): GlobalSearchScope {
        return GlobalSearchScope.EMPTY_SCOPE
    }

    override fun searchScope(module: Module): GlobalSearchScope {
        return module.moduleScope.union(module.moduleContentScope)
    }

    override fun getRoots(project: Project): Iterable<VirtualFile> {
        return listOf()
    }

    override fun getRoots(module: Module): Iterable<VirtualFile> {
        val rootManager = ModuleRootManager.getInstance(module)
        return (rootManager.sourceRoots.takeIf { it.isNotEmpty() } ?: rootManager.contentRoots).asIterable()
    }
}
