package io.kanro.idea.plugin.protobuf.decompile

import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRoot
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootProvider

class DecompiledRootProvider : ProtobufRootProvider {
    override fun id(): String {
        return "decompiled"
    }

    override fun getProtobufRoots(context: PsiElement): List<ProtobufRoot> {
        return listOf(ProtobufRoot(null, DecompiledFileManager.root()))
    }

    override fun modificationTracker(context: PsiElement): ModificationTracker {
        return ModificationTracker.NEVER_CHANGED
    }
}
