package io.kanro.idea.plugin.protobuf.lang.root

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope

class EmbeddedRootProvider : ProtobufRootProvider {
    override fun id(): String {
        return "embedded"
    }

    override fun roots(context: PsiElement): List<ProtobufRoot> {
        val proto = this.javaClass.classLoader.getResource("io/kanro/idea/plugin/protobuf/proto") ?: return listOf()
        return VirtualFileManager.getInstance().findFileByUrl(VfsUtil.convertFromUrl(proto))?.let {
            listOf(ProtobufRoot(id(), it))
        } ?: listOf()
    }

    override fun searchScope(context: PsiElement): GlobalSearchScope? {
        return GlobalSearchScope.filesScope(context.project, roots(context).map { it.root })
    }
}
