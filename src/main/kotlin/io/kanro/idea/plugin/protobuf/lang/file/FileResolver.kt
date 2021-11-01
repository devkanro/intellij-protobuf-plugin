package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope

interface FileResolver {
    companion object : FileResolver {
        var extensionPoint: ExtensionPointName<FileResolver> =
            ExtensionPointName.create("io.kanro.idea.plugin.protobuf.fileResolver")

        fun getImportPath(file: VirtualFile, element: PsiElement): String? {
            val module = ModuleUtil.findModuleForPsiElement(element)
            return if (module != null) {
                getImportPath(file, module, element)
            } else {
                getImportPath(file, element.project, element)
            }
        }

        override fun getImportPath(file: VirtualFile, module: Module, element: PsiElement): String? {
            extensionPoint.extensionList.forEach {
                it.getImportPath(file, module, element)?.let { return it }
            }
            return null
        }

        override fun getImportPath(file: VirtualFile, project: Project, element: PsiElement): String? {
            extensionPoint.extensionList.forEach {
                it.getImportPath(file, project, element)?.let { return it }
            }
            return null
        }

        fun resolveFile(path: String, element: PsiElement): Iterable<VirtualFile> {
            val module = ModuleUtil.findModuleForPsiElement(element)
            return if (module != null) {
                findFile(path, module, element)
            } else {
                findFile(path, element.project, element)
            }
        }

        override fun findFile(path: String, project: Project, element: PsiElement): Iterable<VirtualFile> {
            return extensionPoint.extensionList.asSequence().flatMap {
                it.findFile(path, project, element).asSequence()
            }.asIterable()
        }

        override fun findFile(path: String, module: Module, element: PsiElement): Iterable<VirtualFile> {
            return extensionPoint.extensionList.asSequence().flatMap {
                it.findFile(path, module, element).asSequence()
            }.asIterable()
        }

        fun collectProtobuf(path: String, element: PsiElement): Iterable<VirtualFile> {
            val module = ModuleUtil.findModuleForPsiElement(element)
            return if (module != null) {
                collectProtobuf(path, module, element)
            } else {
                collectProtobuf(path, element.project, element)
            }
        }

        override fun collectProtobuf(path: String, module: Module, element: PsiElement): Iterable<VirtualFile> {
            return extensionPoint.extensionList.asSequence().flatMap {
                it.collectProtobuf(path, module, element).asSequence()
            }.asIterable()
        }

        override fun collectProtobuf(path: String, project: Project, element: PsiElement): Iterable<VirtualFile> {
            return extensionPoint.extensionList.asSequence().flatMap {
                it.collectProtobuf(path, project, element).asSequence()
            }.asIterable()
        }

        override fun searchScope(project: Project, element: PsiElement): GlobalSearchScope {
            val scopes = extensionPoint.extensionList.map {
                it.searchScope(project, element)
            }
            return GlobalSearchScope.union(scopes)
        }

        override fun searchScope(module: Module, element: PsiElement): GlobalSearchScope {
            val scopes = extensionPoint.extensionList.map {
                it.searchScope(module, element)
            }
            return GlobalSearchScope.union(scopes)
        }

        fun searchScope(element: PsiElement): GlobalSearchScope {
            val module = ModuleUtil.findModuleForPsiElement(element)
            return if (module != null) {
                searchScope(module, element)
            } else {
                searchScope(element.project, element)
            }
        }
    }

    fun getImportPath(file: VirtualFile, project: Project, element: PsiElement): String?

    fun getImportPath(file: VirtualFile, module: Module, element: PsiElement): String?

    fun findFile(path: String, project: Project, element: PsiElement): Iterable<VirtualFile>

    fun findFile(path: String, module: Module, element: PsiElement): Iterable<VirtualFile>

    fun collectProtobuf(path: String, project: Project, element: PsiElement): Iterable<VirtualFile>

    fun collectProtobuf(path: String, module: Module, element: PsiElement): Iterable<VirtualFile>

    fun searchScope(project: Project, element: PsiElement): GlobalSearchScope

    fun searchScope(module: Module, element: PsiElement): GlobalSearchScope
}
