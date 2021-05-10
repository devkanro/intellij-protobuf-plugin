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
                getImportPath(file, module)
            } else {
                getImportPath(file, element.project)
            }
        }

        override fun getImportPath(file: VirtualFile, module: Module): String? {
            extensionPoint.extensionList.forEach {
                it.getImportPath(file, module)?.let { return it }
            }
            return null
        }

        override fun getImportPath(file: VirtualFile, project: Project): String? {
            extensionPoint.extensionList.forEach {
                it.getImportPath(file, project)?.let { return it }
            }
            return null
        }

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

        fun collectProtobuf(path: String, element: PsiElement): Iterable<VirtualFile> {
            val module = ModuleUtil.findModuleForPsiElement(element)
            return if (module != null) {
                collectProtobuf(path, module)
            } else {
                collectProtobuf(path, element.project)
            }
        }

        override fun collectProtobuf(path: String, module: Module): Iterable<VirtualFile> {
            return extensionPoint.extensionList.asSequence().flatMap {
                it.collectProtobuf(path, module).asSequence()
            }.asIterable()
        }

        override fun collectProtobuf(path: String, project: Project): Iterable<VirtualFile> {
            return extensionPoint.extensionList.asSequence().flatMap {
                it.collectProtobuf(path, project).asSequence()
            }.asIterable()
        }

        override fun searchScope(project: Project): GlobalSearchScope {
            val scopes = extensionPoint.extensionList.map {
                it.searchScope(project)
            }
            return GlobalSearchScope.union(scopes)
        }

        override fun searchScope(module: Module): GlobalSearchScope {
            val scopes = extensionPoint.extensionList.map {
                it.searchScope(module)
            }
            return GlobalSearchScope.union(scopes)
        }

        fun searchScope(element: PsiElement): GlobalSearchScope {
            val module = ModuleUtil.findModuleForPsiElement(element)
            return if (module != null) {
                searchScope(module)
            } else {
                searchScope(element.project)
            }
        }
    }

    fun getImportPath(file: VirtualFile, project: Project): String?

    fun getImportPath(file: VirtualFile, module: Module): String?

    fun findFile(path: String, project: Project): Iterable<VirtualFile>

    fun findFile(path: String, module: Module): Iterable<VirtualFile>

    fun collectProtobuf(path: String, project: Project): Iterable<VirtualFile>

    fun collectProtobuf(path: String, module: Module): Iterable<VirtualFile>

    fun searchScope(project: Project): GlobalSearchScope

    fun searchScope(module: Module): GlobalSearchScope
}
