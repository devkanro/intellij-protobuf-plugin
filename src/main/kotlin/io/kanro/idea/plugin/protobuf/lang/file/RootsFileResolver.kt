package io.kanro.idea.plugin.protobuf.lang.file

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem
import io.kanro.idea.plugin.protobuf.lang.ProtobufFileType

abstract class RootsFileResolver : FileResolver {
    protected abstract fun getRoots(project: Project): Iterable<VirtualFile>

    protected abstract fun getRoots(module: Module): Iterable<VirtualFile>

    override fun findFile(path: String, project: Project): Iterable<VirtualFile> {
        return findFile(path, getRoots(project))
    }

    override fun findFile(path: String, module: Module): Iterable<VirtualFile> {
        return findFile(path, getRoots(module))
    }

    override fun collectProtobuf(path: String, project: Project): Iterable<VirtualFile> {
        return collectProtobuf(path, project, getRoots(project))
    }

    override fun collectProtobuf(path: String, module: Module): Iterable<VirtualFile> {
        return collectProtobuf(path, module.project, getRoots(module))
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
