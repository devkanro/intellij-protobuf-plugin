package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider
import com.intellij.openapi.roots.SyntheticLibrary
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.PlatformIcons
import com.intellij.util.indexing.FileBasedIndex
import io.kanro.idea.plugin.protobuf.buf.BufLockIndex
import javax.swing.Icon

class BufLibraryRootsProvider : AdditionalLibraryRootsProvider() {
    override fun getAdditionalProjectLibraries(project: Project): MutableCollection<SyntheticLibrary> {
        return mutableListOf()
        return FileBasedIndex.getInstance().getAllKeys(BufLockIndex.NAME, project).mapNotNull {
            val data = it.split('/')
            if (data.size != 4) return@mapNotNull null
            BufSyntheticLibrary(data[0], data[1], data[2], data[3])
        }.toMutableList()
    }

    override fun getRootsToWatch(project: Project): MutableCollection<VirtualFile> {
        return mutableListOf()
        return FileBasedIndex.getInstance().getAllKeys(BufLockIndex.NAME, project).mapNotNull {
            val data = it.split('/')
            if (data.size != 4) return@mapNotNull null
            VirtualFileManager.getInstance()
                .findFileByNioPath(BufLockIndex.getRootForDepModel(data[0], data[1], data[2], data[3]))
        }.toMutableList()
    }
}

data class BufSyntheticLibrary(
    val remote: String,
    val owner: String,
    val repo: String,
    val commit: String
) : SyntheticLibrary(), ItemPresentation {
    override fun getSourceRoots(): MutableCollection<VirtualFile> {
        return listOfNotNull(
            VirtualFileManager.getInstance()
                .findFileByNioPath(BufLockIndex.getRootForDepModel(remote, owner, repo, commit))
        ).toMutableList()
    }

    override fun getPresentableText(): String {
        return "Buf: $remote:$owner:$repo:$commit"
    }

    override fun getIcon(unused: Boolean): Icon {
        return PlatformIcons.LIBRARY_ICON
    }
}
