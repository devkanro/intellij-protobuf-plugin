package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.components.service
import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRoot
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootProvider
import kotlin.io.path.Path

class BufRootProvider : ProtobufRootProvider {
    override fun id(): String {
        return "bufRoot"
    }

    override fun getProtobufRoots(context: PsiElement): List<ProtobufRoot> {
        val project = context.project
        val fileManager = project.service<BufFileManager>()

        val module = fileManager.findModuleFromPsiElement(context) ?: return listOf()
        val workspace = fileManager.state.workspaces.firstOrNull { module.path in it.roots }

        return (workspace?.roots ?: listOfNotNull(module.path)).mapNotNull {
            VirtualFileManager.getInstance().findFileByNioPath(Path(it))
        }.map {
            ProtobufRoot(null, it)
        }
    }

    override fun modificationTracker(context: PsiElement): ModificationTracker {
        return context.project.service<BufFileManager>().state
    }
}
