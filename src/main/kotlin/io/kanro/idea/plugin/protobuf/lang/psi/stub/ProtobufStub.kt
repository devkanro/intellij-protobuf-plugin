package io.kanro.idea.plugin.protobuf.lang.psi.stub

import com.intellij.psi.PsiFile
import com.intellij.psi.StubBuilder
import com.intellij.psi.stubs.DefaultStubBuilder
import com.intellij.psi.stubs.PsiFileStub
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.tree.IStubFileElementType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufFileStubImpl

interface ProtobufStub<T : ProtobufElement> : StubElement<T>

interface ProtobufFileStub : PsiFileStub<ProtobufFile> {
    object Type : IStubFileElementType<ProtobufFileStub>("PROTO_FILE", ProtobufLanguage) {
        override fun getExternalId(): String {
            return "protobuf.file"
        }

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): ProtobufFileStub {
            return ProtobufFileStubImpl(null)
        }

        override fun getBuilder(): StubBuilder {
            return StubBuilder
        }
    }

    object StubBuilder : DefaultStubBuilder() {
        override fun createStubForFile(file: PsiFile): StubElement<*> {
            return ProtobufFileStubImpl(file as ProtobufFile)
        }
    }
}
