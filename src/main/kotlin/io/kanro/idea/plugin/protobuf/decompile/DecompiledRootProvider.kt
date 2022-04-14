package io.kanro.idea.plugin.protobuf.decompile

import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRoot
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootProvider

class DecompiledRootProvider : ProtobufRootProvider {
    override fun id(): String {
        return "decompiled"
    }

    override fun roots(context: PsiElement): List<ProtobufRoot> {
        return listOf(ProtobufRoot(null, DecompiledFileManager.root()))
    }
}
