package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider
import com.intellij.openapi.roots.SyntheticLibrary
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PlatformIcons
import java.util.Stack
import javax.swing.Icon
import kotlin.io.path.Path

class BufLibraryRootsProvider : AdditionalLibraryRootsProvider() {
    override fun getAdditionalProjectLibraries(project: Project): MutableCollection<SyntheticLibrary> {
        val manager = project.service<BufFileManager>()
        val libraries = manager.state.libraries.associateBy { it.reference }
        val resolved = mutableSetOf<String>()
        val required = Stack<BufFileManager.State.Dependency>()
        val result = mutableListOf<SyntheticLibrary>()
        required += manager.state.modules.flatMap { it.lockedDependencies }

        while (required.isNotEmpty()) {
            val dep = required.pop()
            val name = dep.name()
            if (name in resolved) continue
            resolved += name
            val lib = libraries[name] ?: continue
            result += BufSyntheticLibrary(lib)
        }

        return result
    }

    override fun getRootsToWatch(project: Project): MutableCollection<VirtualFile> {
        val manager = project.service<BufFileManager>()
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