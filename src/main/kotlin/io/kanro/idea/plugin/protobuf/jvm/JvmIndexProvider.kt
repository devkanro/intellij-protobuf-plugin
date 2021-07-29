package io.kanro.idea.plugin.protobuf.jvm

import com.intellij.psi.stubs.IndexSink
import io.kanro.idea.plugin.protobuf.jvm.name.JvmNamespace
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufIndexProvider
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.qualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub

class JvmIndexProvider : ProtobufIndexProvider {
    override fun buildIndex(stub: ProtobufStub<*>, sink: IndexSink) {
        if (stub !is ProtobufDefinitionStub) return

        JvmNamespace.qualifiedName(stub)?.let {
            sink.occurrence(JvmNameIndex.key, it.toString())
        }
    }
}
