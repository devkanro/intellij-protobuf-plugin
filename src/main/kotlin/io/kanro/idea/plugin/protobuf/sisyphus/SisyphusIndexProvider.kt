package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufIndexProvider
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ExternalProtobufNamespace
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufNamedStub

class SisyphusIndexProvider : ProtobufIndexProvider {
    companion object {
        const val key = "sisyphus-name"
    }

    override fun buildIndex(stub: ProtobufStub<*>, sink: IndexSink) {
        if (stub is ProtobufNamedStub) {
            ExternalProtobufNamespace.extensionPoint.extensionList.forEach {
                it.qualifiedName(stub)
            }

            stub.externalQualifiedName(key())?.let {
                sink.occurrence(SisyphusNameIndex.key, it.toString())
            }
        }
    }
}

object SisyphusNamespace : ExternalProtobufNamespace {
    override fun packageName(file: ProtobufFile): QualifiedName? {

    }

    override fun packageName(file: ProtobufFileStub): QualifiedName? {
        file.externalScope()
    }

    override fun name(element: ProtobufNamedElement): String? {
        TODO("Not yet implemented")
    }

    override fun name(element: ProtobufNamedStub): String? {
        TODO("Not yet implemented")
    }

    override fun qualifiedName(element: ProtobufNamedElement): QualifiedName? {
        TODO("Not yet implemented")
    }

    override fun qualifiedName(element: ProtobufNamedStub): QualifiedName? {
        TODO("Not yet implemented")
    }
}