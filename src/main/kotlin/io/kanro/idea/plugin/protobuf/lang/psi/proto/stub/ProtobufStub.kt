package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.DefaultStubBuilder
import com.intellij.psi.stubs.PsiFileStub
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.tree.IStubFileElementType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufFileStubImpl
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufScopeStub

interface ProtobufStub<T : PsiElement> : StubElement<T> {
    fun data(index: Int): String

    fun externalData(key: String): String?

    fun writeTo(dataStream: StubOutputStream)
}

interface ProtobufFileStub :
    PsiFileStub<ProtobufFile>,
    ProtobufStub<ProtobufFile>,
    ProtobufScopeStub {
    object Type : IStubFileElementType<ProtobufFileStub>("PROTO_FILE", ProtobufLanguage) {
        override fun getStubVersion(): Int {
            return 1
        }

        override fun getExternalId(): String {
            return "protobuf.file"
        }

        override fun deserialize(
            dataStream: StubInputStream,
            parentStub: StubElement<*>?,
        ): ProtobufFileStub {
            return ProtobufFileStubImpl(null, dataStream.readStringArray(), dataStream.readMap())
        }

        override fun serialize(
            stub: ProtobufFileStub,
            dataStream: StubOutputStream,
        ) {
            stub.writeTo(dataStream)
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

fun StubInputStream.readStringArray(): Array<String> {
    return (0 until readVarInt()).map {
        readNameString() ?: throw IllegalStateException("Wrong stub data")
    }.toTypedArray()
}

fun StubOutputStream.writeStringArray(data: Array<String>) {
    writeVarInt(data.size)
    data.forEach {
        writeName(it)
    }
}

fun StubInputStream.readMap(): Map<String, String> {
    return (0 until readVarInt()).associate {
        val key = readNameString() ?: throw IllegalStateException("Wrong stub data")
        val value = readNameString() ?: throw IllegalStateException("Wrong stub data")

        key to value
    }
}

fun StubOutputStream.writeMap(data: Map<String, String>) {
    writeVarInt(data.size)
    data.forEach { (key, value) ->
        writeName(key)
        writeName(value)
    }
}
