package io.kanro.idea.plugin.protobuf.golang

import com.goide.project.GoRootsProvider
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.file.RootsFileResolver

class GoRootResolver : RootsFileResolver() {
    override fun getRoots(project: Project, element: PsiElement): Iterable<VirtualFile> {
        return GoRootsProvider.EP_NAME.extensionList.flatMap { it.getGoPathSourcesRoots(project, null) }
    }

    override fun getRoots(module: Module, element: PsiElement): Iterable<VirtualFile> {
        return GoRootsProvider.EP_NAME.extensionList.flatMap { it.getGoPathSourcesRoots(module.project, module) }
    }
}

