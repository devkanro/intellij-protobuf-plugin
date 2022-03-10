package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.file.RootsFileResolver

class BufRootResolver : RootsFileResolver() {
    override fun getRoots(project: Project, element: PsiElement): Iterable<VirtualFile> {
        return listOfNotNull(BufModuleIndex.getModuleBufYaml(project, element)?.parent)
    }

    override fun getRoots(module: Module, element: PsiElement): Iterable<VirtualFile> {
        return getRoots(module.project, element)
    }
}

class BufDepRootsResolver : RootsFileResolver() {
    override fun getRoots(project: Project, element: PsiElement): Iterable<VirtualFile> {
        return BufDepIndex.getModuleDepRoots(project, element)
    }

    override fun getRoots(module: Module, element: PsiElement): Iterable<VirtualFile> {
        return getRoots(module.project, element)
    }
}