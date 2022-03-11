package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRoot
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootProvider

class BufDepRootsProvider : ProtobufRootProvider {
    override fun id(): String {
        return "bufDeps"
    }

    override fun getProtobufRoots(context: PsiElement): List<ProtobufRoot> {
        val project = context.project
        val buf = BufModuleIndex.getModuleBufYaml(project, context) ?: return listOf()
        val lock = BufLockIndex.getBufLock(project, buf) ?: return listOf()
        return BufLockIndex.getModuleDepModules(project, lock).map {
            ProtobufRoot(BufModuleIndex.getModuleName(project, it), buf.parent)
        }
    }

    override fun modificationTracker(context: PsiElement): ModificationTracker {
        val buf =
            BufModuleIndex.getModuleBufYaml(context.project, context) ?: return BufConfigurationModificationTracker
        return BufLockIndex.getBufLock(context.project, buf) ?: BufConfigurationModificationTracker
    }
}
