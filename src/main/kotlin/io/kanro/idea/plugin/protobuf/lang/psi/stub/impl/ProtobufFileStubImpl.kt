package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub

class ProtobufFileStubImpl(file: ProtobufFile?) : PsiFileStubImpl<ProtobufFile>(file), ProtobufFileStub {
    override fun scope(): QualifiedName {
        return QualifiedName.fromComponents(childrenStubs.filterIsInstance<ProtobufPackageNameStub>().map { it.name() })
    }
}
