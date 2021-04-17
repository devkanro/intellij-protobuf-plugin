package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubOutputStream

abstract class ProtobufStubBase<T : PsiElement>(
    private val data: Array<String>,
    parent: StubElement<*>?,
    type: IStubElementType<*, *>
) : StubBase<T>(parent, type) {
    fun data(index: Int): String {
        return data[index]
    }

    fun writeTo(dataStream: StubOutputStream) {
        dataStream.writeVarInt(data.size)
        data.forEach {
            dataStream.writeName(it)
        }
    }
}
