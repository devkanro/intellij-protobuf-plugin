package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope

class ModuleSourceProtobufFileResolver : RootsFileResolver() {
    override fun searchScope(project: Project, element: PsiElement): GlobalSearchScope {
        return GlobalSearchScope.EMPTY_SCOPE
    }

    override fun searchScope(module: Module, element: PsiElement): GlobalSearchScope {
        return module.moduleScope.union(module.moduleContentScope)
    }

    override fun getRoots(project: Project, element: PsiElement): Iterable<VirtualFile> {
        return listOf()
    }

    override fun getRoots(module: Module, element: PsiElement): Iterable<VirtualFile> {
        val rootManager = ModuleRootManager.getInstance(module)
        return rootManager.sourceRoots.asIterable()
    }
}
