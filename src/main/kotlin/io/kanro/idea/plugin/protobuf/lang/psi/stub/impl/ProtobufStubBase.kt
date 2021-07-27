package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubOutputStream
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.writeMap
import io.kanro.idea.plugin.protobuf.lang.psi.stub.writeStringArray

abstract class ProtobufStubBase<T : PsiElement>(
    private val data: Array<String>,
    private val externalData: Map<String, String>,
    parent: StubElement<*>?,
    type: IStubElementType<*, *>
) : StubBase<T>(parent, type), ProtobufStub<T> {
    override fun data(index: Int): String {
        return data[index]
    }

    override fun externalData(key: String): String? {
        return externalData[key]
    }

    override fun writeTo(dataStream: StubOutputStream) {
        dataStream.writeStringArray(data)
        dataStream.writeMap(externalData)
    }
}
