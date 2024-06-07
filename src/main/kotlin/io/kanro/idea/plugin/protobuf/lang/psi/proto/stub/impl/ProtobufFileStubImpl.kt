package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl

import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.writeMap
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.writeStringArray

class ProtobufFileStubImpl(
    file: ProtobufFile?,
    private val data: Array<String> = file?.stubData() ?: arrayOf(),
    private val external: Map<String, String> = file?.stubExternalData() ?: mapOf(),
) :
    PsiFileStubImpl<ProtobufFile>(file),
    io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufFileStub {
    override fun scope(): QualifiedName {
        return QualifiedName.fromComponents(childrenStubs.filterIsInstance<ProtobufPackageNameStub>().map { it.name() })
    }

    override fun data(index: Int): String {
        return data[index]
    }

    override fun externalData(key: String): String? {
        return external[key]
    }

    override fun writeTo(dataStream: StubOutputStream) {
        dataStream.writeStringArray(data)
        dataStream.writeMap(external)
    }
}
