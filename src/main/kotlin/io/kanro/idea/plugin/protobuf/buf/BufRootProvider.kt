package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.components.service
import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.lang.root.CachedProtobufRootProvider
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRoot
import kotlin.io.path.Path

class BufRootProvider : CachedProtobufRootProvider() {
    override fun id(): String {
        return "bufRoot"
    }
    private fun getModuleContextModules(
        fileManager: BufFileManager,
        context: PsiElement
    ): List<BufFileManager.State.Module>? {
        val module = fileManager.findModuleFromPsiElement(context) ?: return null
        val workspace = fileManager.state.workspaces.firstOrNull { module.path in it.roots }
        return workspace?.let { fileManager.findModulesInWorkspace(it) } ?: listOf(module)
    }

    private fun getLibraryContextModules(
        fileManager: BufFileManager,
        context: PsiElement
    ): List<BufFileManager.State.Module>? {
        val library = fileManager.findLibraryFromPsiElement(context) ?: return null
        return listOf(library)
    }

    override fun collectRoots(context: PsiElement): List<ProtobufRoot> {
        val project = context.project
        val fileManager = project.service<BufFileManager>()

        val contextModules = getModuleContextModules(fileManager, context)
            ?: getLibraryContextModules(fileManager, context) ?: return listOf()
        val localDeps = contextModules.mapNotNull { it.name }.toSet()

        val roots = contextModules.mapNotNull {
            rootForModule(it)
        }.toMutableList()

        val remoteDeps = contextModules.flatMap { it.lockedDependencies }.filter {
            it.nameWithoutCommit() !in localDeps
        }
        roots += fileManager.resolveDependencies(remoteDeps).mapNotNull { rootForModule(it) }
        return roots
    }

    private fun rootForModule(module: BufFileManager.State.Module?): ProtobufRoot? {
        module ?: return null
        val path = module.path ?: return null
        return VirtualFileManager.getInstance().findFileByNioPath(Path(path))?.let {
            ProtobufRoot(module.reference, it)
        }
    }

    override fun modificationTracker(context: PsiElement): ModificationTracker {
        return context.project.service<BufFileManager>().state
    }
}
