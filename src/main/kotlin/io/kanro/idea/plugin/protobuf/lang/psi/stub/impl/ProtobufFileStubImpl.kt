package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.writeMap
import io.kanro.idea.plugin.protobuf.lang.psi.stub.writeStringArray

class ProtobufFileStubImpl(
    file: ProtobufFile?,
    private val data: Array<String>,
    private val external: Map<String, String>
) :
    PsiFileStubImpl<ProtobufFile>(file),
    ProtobufFileStub {
    constructor(file: ProtobufFile?) : this(file, arrayOf(), mapOf())

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
