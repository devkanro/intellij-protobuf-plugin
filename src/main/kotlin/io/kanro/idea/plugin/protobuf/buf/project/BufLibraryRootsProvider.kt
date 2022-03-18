package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.components.serviceIfCreated
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider
import com.intellij.openapi.roots.SyntheticLibrary
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PlatformIcons
import javax.swing.Icon
import kotlin.io.path.Path

class BufLibraryRootsProvider : AdditionalLibraryRootsProvider() {
    override fun getAdditionalProjectLibraries(project: Project): MutableCollection<SyntheticLibrary> {
        val manager = project.serviceIfCreated<BufFileManager>() ?: return mutableListOf()
        return manager.resolveDependencies(manager.state.modules.flatMap { it.lockedDependencies }).map {
            BufSyntheticLibrary(it)
        }.toMutableList()
    }

    override fun getRootsToWatch(project: Project): MutableCollection<VirtualFile> {
        val manager = project.serviceIfCreated<BufFileManager>() ?: return mutableListOf()
        return mutableListOf<VirtualFile>().apply {
            manager.cacheRoot()?.let { this += it }
        }
    }

    data class BufSyntheticLibrary(val module: BufFileManager.State.Module) : SyntheticLibrary(), ItemPresentation {
        override fun getSourceRoots(): MutableCollection<VirtualFile> {
            return mutableListOf<VirtualFile>().apply {
                module.path?.let { VfsUtil.findFile(Path(it), true) }?.let {
                    this += it
                }
            }
        }

        override fun getPresentableText(): String {
            return "Buf: ${module.reference}"
        }

        override fun getIcon(unused: Boolean): Icon {
            return PlatformIcons.LIBRARY_ICON
        }
    }
}
