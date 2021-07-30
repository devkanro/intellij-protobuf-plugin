package io.kanro.idea.plugin.protobuf.java

import com.intellij.psi.stubs.IndexSink
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufIndexProvider
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufServiceStub

class JavaIndexProvider : ProtobufIndexProvider {
    override fun buildIndex(stub: ProtobufStub<*>, sink: IndexSink) {
        when (stub) {
            is ProtobufServiceStub -> {
                sink.occurrence(JavaNameIndex.key, stub.fullImplBaseName().toString())
                sink.occurrence(JavaNameIndex.key, stub.fullCoroutineImplBaseName().toString())
            }
            is ProtobufRpcStub -> {
                sink.occurrence(
                    JavaNameIndex.key,
                    stub.owner()?.fullImplBaseName()?.append(stub.methodName()).toString()
                )
                sink.occurrence(
                    JavaNameIndex.key,
                    stub.owner()?.fullCoroutineImplBaseName()?.append(stub.methodName()).toString()
                )
            }
        }
    }
}
