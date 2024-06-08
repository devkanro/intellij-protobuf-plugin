package io.kanro.idea.plugin.protobuf.golang

import com.intellij.psi.stubs.IndexSink
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufIndexProvider
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufServiceStub

class GoIndexProvider : ProtobufIndexProvider {
    override fun buildIndex(
        stub: io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub<*>,
        sink: IndexSink,
    ) {
        when (stub) {
            is ProtobufServiceStub -> {
                sink.occurrence(GoNameIndex.key, stub.clientName() ?: return)
                sink.occurrence(GoNameIndex.key, stub.serverName() ?: return)
                sink.occurrence(GoNameIndex.key, stub.unimplementedName() ?: return)
                sink.occurrence(GoUnimplementedServerNameIndex.key, stub.unimplementedName() ?: return)
            }

            is ProtobufRpcStub -> {
                sink.occurrence(GoNameIndex.key, stub.funcName() ?: return)
                sink.occurrence(
                    GoUnimplementedServerNameIndex.key,
                    "${stub.owner()?.unimplementedName()}.${stub.funcName()}",
                )
            }
        }
    }
}
