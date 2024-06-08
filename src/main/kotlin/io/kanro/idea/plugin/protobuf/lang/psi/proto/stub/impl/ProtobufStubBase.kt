package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubOutputStream
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.writeMap
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.writeStringArray

abstract class ProtobufStubBase<T : PsiElement>(
    private val data: Array<String>,
    private val externalData: Map<String, String>,
    parent: StubElement<*>?,
    type: IStubElementType<*, *>,
) : StubBase<T>(parent, type), io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub<T> {
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
