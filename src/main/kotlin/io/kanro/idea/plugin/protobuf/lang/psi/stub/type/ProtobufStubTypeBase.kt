package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufStubSupport
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufStubBase
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.QualifiedNameIndex
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.ShortNameIndex
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufNamedStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.readMap
import io.kanro.idea.plugin.protobuf.lang.psi.stub.readStringArray

abstract class ProtobufStubTypeBase<TStub : ProtobufStubBase<TPsi>, TPsi : PsiElement>(
    name: String
) : IStubElementType<TStub, TPsi>(
    name, ProtobufLanguage
) {
    override fun serialize(stub: TStub, dataStream: StubOutputStream) {
        stub.writeTo(dataStream)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): TStub {
        return createStub(dataStream.readStringArray(), dataStream.readMap(), parentStub)
    }

    override fun createStub(psi: TPsi, parentStub: StubElement<out PsiElement>?): TStub {
        if (psi !is ProtobufStubSupport<*, *>) {
            throw IllegalStateException("Psi must implement ProtobufStubSupport")
        }
        return createStub(psi.stubData(), psi.stubExternalData(), parentStub)
    }

    override fun indexStub(stub: TStub, sink: IndexSink) {
        if (stub is ProtobufNamedStub) {
            stub.name()?.let {
                sink.occurrence(ShortNameIndex.key, it)
            }
            stub.qualifiedName()?.let {
                sink.occurrence(QualifiedNameIndex.key, it.toString())
            }
        }
    }

    abstract fun createStub(data: Array<String>, external: Map<String, String>, parentStub: StubElement<*>?): TStub
}
