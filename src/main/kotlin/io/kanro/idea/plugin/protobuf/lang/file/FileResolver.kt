package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement

interface FileResolver {
    companion object : FileResolver {
        var extensionPoint: ExtensionPointName<FileResolver> =
            ExtensionPointName.create("io.kanro.idea.plugin.protobuf.fileResolver")

        fun resolveFile(path: String, element: PsiElement): Iterable<VirtualFile> {
            val module = ModuleUtil.findModuleForPsiElement(element)
            return if (module != null) {
                findFile(path, module)
            } else {
                findFile(path, element.project)
            }
        }

        override fun findFile(path: String, project: Project): Iterable<VirtualFile> {
            return extensionPoint.extensionList.asSequence().flatMap {
                it.findFile(path, project).asSequence()
            }.asIterable()
        }

        override fun findFile(path: String, module: Module): Iterable<VirtualFile> {
            return extensionPoint.extensionList.asSequence().flatMap {
                it.findFile(path, module).asSequence()
            }.asIterable()
        }
    }

    fun findFile(path: String, project: Project): Iterable<VirtualFile>

    fun findFile(path: String, module: Module): Iterable<VirtualFile>
}
