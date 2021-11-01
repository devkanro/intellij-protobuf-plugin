package io.kanro.idea.plugin.protobuf.decompile

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.file.RootsFileResolver

class DecompiledFileResolver : RootsFileResolver() {
    override fun getRoots(project: Project, element: PsiElement): Iterable<VirtualFile> {
        return listOf(DecompiledFileManager.root())
    }

    override fun getRoots(module: Module, element: PsiElement): Iterable<VirtualFile> {
        return listOf(DecompiledFileManager.root())
    }
}
