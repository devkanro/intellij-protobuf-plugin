package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider
import com.intellij.openapi.roots.SyntheticLibrary
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PlatformIcons
import javax.swing.Icon

class BufLibraryRootsProvider : AdditionalLibraryRootsProvider() {
    override fun getAdditionalProjectLibraries(project: Project): MutableCollection<SyntheticLibrary> {
        return mutableListOf()
    }

    override fun getRootsToWatch(project: Project): MutableCollection<VirtualFile> {
        return mutableListOf()
    }
}

data class BufSyntheticLibrary(
    val remote: String,
    val owner: String,
    val repo: String,
    val commit: String
) : SyntheticLibrary(), ItemPresentation {
    override fun getSourceRoots(): MutableCollection<VirtualFile> {
        return mutableListOf()
    }

    override fun getPresentableText(): String {
        return "Buf: $remote:$owner:$repo:$commit"
    }

    override fun getIcon(unused: Boolean): Icon {
        return PlatformIcons.LIBRARY_ICON
    }
}
