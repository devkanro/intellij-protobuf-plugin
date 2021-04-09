package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import com.intellij.psi.PsiElement

interface ProtobufNumbered : ProtobufElement {
    fun numberElement(): PsiElement?

    fun number(): Long?
}

interface ProtobufReservedNumber : ProtobufElement {
    fun range(): LongRange?
}
