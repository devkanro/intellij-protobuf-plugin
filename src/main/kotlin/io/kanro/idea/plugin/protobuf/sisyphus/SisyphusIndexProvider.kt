package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.psi.stubs.IndexSink
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufIndexProvider
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufServiceStub

class SisyphusIndexProvider : ProtobufIndexProvider {
    override fun buildIndex(
        stub: io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub<*>,
        sink: IndexSink,
    ) {
        when (stub) {
            is ProtobufServiceStub -> sink.occurrence(SisyphusNameIndex.key, stub.fullClassName().toString())
            is ProtobufRpcStub ->
                sink.occurrence(
                    SisyphusNameIndex.key,
                    stub.owner()?.fullClassName()?.append(stub.methodName()).toString(),
                )
        }
    }
}
