package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope

class DepModuleSourceProtobufFileResolver : RootsFileResolver() {
    override fun searchScope(project: Project, element: PsiElement): GlobalSearchScope {
        return ProjectScope.getContentScope(project)
    }

    override fun searchScope(module: Module, element: PsiElement): GlobalSearchScope {
        return module.moduleWithDependenciesScope
    }

    override fun getRoots(project: Project, element: PsiElement): Iterable<VirtualFile> {
        return ProjectRootManager.getInstance(project).orderEntries()
            .withoutLibraries()
            .withoutSdk().allSourceRoots.asIterable()
    }

    override fun getRoots(module: Module, element: PsiElement): Iterable<VirtualFile> {
        return ModuleRootManager.getInstance(module).orderEntries()
            .withoutModuleSourceEntries()
            .withoutLibraries().withoutSdk().allSourceRoots.asIterable()
    }
}
