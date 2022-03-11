package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRoot
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootProvider

class BufRootProvider : ProtobufRootProvider {
    override fun id(): String {
        return "bufRoot"
    }

    override fun getProtobufRoots(context: PsiElement): List<ProtobufRoot> {
        val project = context.project
        val buf = BufModuleIndex.getModuleBufYaml(project, context) ?: return listOf()
        val bufWork = BufWorkspaceIndex.getBufWorkspaceYaml(project, buf) ?: return listOf(
            ProtobufRoot(null, buf.parent)
        )
        return BufWorkspaceIndex.getBufWorkspaceModules(project, bufWork).map {
            ProtobufRoot(BufModuleIndex.getModuleName(project, it), buf.parent)
        }
    }

    override fun modificationTracker(context: PsiElement): ModificationTracker {
        return BufModuleIndex.getModuleBufYaml(context.project, context) ?: BufConfigurationModificationTracker
    }
}
