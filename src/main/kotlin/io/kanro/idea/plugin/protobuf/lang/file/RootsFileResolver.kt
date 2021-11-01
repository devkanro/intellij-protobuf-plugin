package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import io.kanro.idea.plugin.protobuf.lang.ProtobufFileType

abstract class RootsFileResolver : FileResolver {
    protected abstract fun getRoots(project: Project, element: PsiElement): Iterable<VirtualFile>

    protected abstract fun getRoots(module: Module, element: PsiElement): Iterable<VirtualFile>

    override fun searchScope(project: Project, element: PsiElement): GlobalSearchScope {
        return GlobalSearchScope.filesScope(project, getRoots(project, element).toList())
    }

    override fun searchScope(module: Module, element: PsiElement): GlobalSearchScope {
        return GlobalSearchScope.filesScope(module.project, getRoots(module, element).toList())
    }

    override fun getImportPath(file: VirtualFile, project: Project, element: PsiElement): String? {
        getRoots(project, element).forEach {
            VfsUtilCore.getRelativePath(file, it)?.let { return it }
        }
        return null
    }

    override fun getImportPath(file: VirtualFile, module: Module, element: PsiElement): String? {
        getRoots(module, element).forEach {
            VfsUtilCore.getRelativePath(file, it)?.let { return it }
        }
        return null
    }

    override fun findFile(path: String, project: Project, element: PsiElement): Iterable<VirtualFile> {
        return findFile(path, getRoots(project, element))
    }

    override fun findFile(path: String, module: Module, element: PsiElement): Iterable<VirtualFile> {
        return findFile(path, getRoots(module, element))
    }

    override fun collectProtobuf(path: String, project: Project, element: PsiElement): Iterable<VirtualFile> {
        return collectProtobuf(path, project, getRoots(project, element))
    }

    override fun collectProtobuf(path: String, module: Module, element: PsiElement): Iterable<VirtualFile> {
        return collectProtobuf(path, module.project, getRoots(module, element))
    }

    protected open fun findFile(path: String, roots: Iterable<VirtualFile>): Iterable<VirtualFile> {
        return roots.mapNotNull {
            it.findFileByRelativePath(path)?.takeIf {
                it.exists() && it.fileType is ProtobufFileType
            }
        }
    }

    protected open fun collectProtobuf(
        path: String,
        project: Project,
        roots: Iterable<VirtualFile>
    ): Iterable<VirtualFile> {
        val result = mutableListOf<VirtualFile>()
        roots.forEach {
            if (!checkRootContainsProtobufFile(it)) return@forEach
            val directory = it.findFileByRelativePath(path) ?: return@forEach

            if (!directory.isDirectory) return@forEach
            for (child in directory.children) {
                if (child.isDirectory && checkDirectoryContainsProtobufFile(child)) {
                    result += child
                } else if (child.fileType is ProtobufFileType) {
                    result += child
                }
            }
        }
        return result
    }

    private fun checkRootContainsProtobufFile(root: VirtualFile): Boolean {
        (root.fileSystem as? ArchiveFileSystem)?.let {
            val jarFile = it.getLocalByEntry(root) ?: return@let
            synchronized(jarFile) {
                val existing = jarFile.getUserData(CachedJarProtoScanResult)
                val jarRoot = it.getRootByEntry(jarFile) ?: return@let
                if (existing?.version == jarFile.modificationCount) {
                    return existing.value
                }
                val cache =
                    CachedJarProtoScanResult(jarFile.modificationCount, checkDirectoryContainsProtobufFile(jarRoot))
                jarFile.putUserData(CachedJarProtoScanResult, cache)
                return cache.value
            }
        }
        return checkDirectoryContainsProtobufFile(root)
    }

    private class CachedJarProtoScanResult(val version: Long, val value: Boolean) {
        companion object : Key<CachedJarProtoScanResult>(CachedJarProtoScanResult::class.java.name)
    }

    private fun checkDirectoryContainsProtobufFile(file: VirtualFile): Boolean {
        for (child in file.children) {
            if (child.isDirectory) {
                if (checkDirectoryContainsProtobufFile(child)) {
                    return true
                }
            } else if (child.fileType is ProtobufFileType) {
                return true
            }
        }
        return false
    }
}
