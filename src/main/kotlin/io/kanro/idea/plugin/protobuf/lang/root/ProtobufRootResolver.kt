package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import io.kanro.idea.plugin.protobuf.lang.ProtobufFileType

object ProtobufRootResolver {
    private fun Sequence<Pair<String?, List<ProtobufRoot>>>.distinctRoots(): List<VirtualFile> {
        val registeredRootsSet = mutableSetOf<String>()
        val registeredRootSet = mutableSetOf<String>()

        return this.filter {
            it.second.isNotEmpty()
        }.filter { (name, _) ->
            if (name == null) return@filter true
            (name !in registeredRootsSet).apply {
                registeredRootsSet += name
            }
        }.flatMap { it.second }.filter {
            if (it.name == null) return@filter true
            (it.name !in registeredRootSet).apply {
                registeredRootSet += it.name
            }
        }.map {
            it.root
        }.toList()
    }

    private fun getAvailableRoots(context: PsiElement): List<VirtualFile> {
        val file = context.containingFile
        val providers = ProtobufRootProvider.extensionPoint.extensionList.toTypedArray()
        return providers.asSequence().map {
            it.id() to it.roots(file)
        }.distinctRoots()
    }

    fun getImportPath(file: VirtualFile, context: PsiElement): String? {
        getAvailableRoots(context).forEach {
            VfsUtilCore.getRelativePath(file, it)?.let { return it }
        }
        return null
    }

    fun findFile(path: String, context: PsiElement): Iterable<VirtualFile> {
        return findFile(path, getAvailableRoots(context))
    }

    fun collectProtobuf(path: String, context: PsiElement): Iterable<VirtualFile> {
        return collectProtobuf(path, getAvailableRoots(context))
    }

    fun searchScope(context: PsiElement): GlobalSearchScope {
        val file = context.containingFile
        val providers = ProtobufRootProvider.extensionPoint.extensionList
        return GlobalSearchScope.union(providers.mapNotNull { it.searchScope(file) })
    }

    private fun findFile(path: String, roots: Iterable<VirtualFile>): Iterable<VirtualFile> {
        return roots.mapNotNull {
            it.findFileByRelativePath(path)?.takeIf {
                it.exists() && it.fileType is ProtobufFileType
            }
        }
    }

    private fun collectProtobuf(
        path: String,
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
