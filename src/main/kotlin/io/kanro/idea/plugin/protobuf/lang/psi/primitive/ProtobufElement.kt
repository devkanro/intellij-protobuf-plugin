package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile

interface ProtobufElement : PsiElement {
    fun file(): ProtobufFile {
        return containingFile.originalFile as ProtobufFile
    }
}

interface ProtobufStatement : ProtobufElement

interface ProtobufFragment : ProtobufElement

interface ProtobufBlock : ProtobufElement
