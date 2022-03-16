package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement

class EmbeddedRootProvider : ProtobufRootProvider {
    override fun id(): String {
        return "embedded"
    }

    override fun getProtobufRoots(context: PsiElement): List<ProtobufRoot> {
        val proto = this.javaClass.classLoader.getResource("io/kanro/idea/plugin/protobuf/proto") ?: return listOf()
        return VirtualFileManager.getInstance().findFileByUrl(VfsUtil.convertFromUrl(proto))?.let {
            listOf(ProtobufRoot(id(), it))
        } ?: listOf()
    }

    override fun modificationTracker(context: PsiElement): ModificationTracker {
        return ModificationTracker.NEVER_CHANGED
    }
}
