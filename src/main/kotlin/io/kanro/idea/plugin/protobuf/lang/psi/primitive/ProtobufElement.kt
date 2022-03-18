package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver

interface ProtobufElement : PsiElement {
    fun file(): ProtobufFile {
        return containingFile.originalFile as ProtobufFile
    }

    fun importPath(context: ProtobufFile? = null): String? {
        val file = file()
        return ProtobufRootResolver.getImportPath(file.virtualFile, context ?: file)
    }
}
