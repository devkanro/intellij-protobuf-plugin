package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.psi.stubs.IndexSink
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufIndexProvider
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.qualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.sisyphus.name.SisyphusClientNamespace
import io.kanro.idea.plugin.protobuf.sisyphus.name.SisyphusNamespace

class SisyphusIndexProvider : ProtobufIndexProvider {
    override fun buildIndex(stub: ProtobufStub<*>, sink: IndexSink) {
        if (stub !is ProtobufDefinitionStub) return

        SisyphusNamespace.qualifiedName(stub)?.let {
            sink.occurrence(SisyphusNameIndex.key, it.toString())
        }

        SisyphusClientNamespace.qualifiedName(stub)?.let {
            sink.occurrence(SisyphusNameIndex.key, it.toString())
        }
    }
}
