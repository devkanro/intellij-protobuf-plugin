package io.kanro.idea.plugin.protobuf.grpc.index

import com.intellij.psi.stubs.IndexSink
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufIndexProvider
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufMessageStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufServiceStub

class ServiceMethodIndexProvider : ProtobufIndexProvider {
    override fun buildIndex(
        stub: io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub<*>,
        sink: IndexSink,
    ) {
        when (stub) {
            is ProtobufMessageStub -> {
                stub.name()?.let {
                    sink.occurrence(MessageShortNameIndex.key, it)
                }
            }

            is ProtobufServiceStub -> {
                stub.name()?.let {
                    sink.occurrence(ServiceShortNameIndex.key, it)
                }
                stub.qualifiedName()?.let {
                    sink.occurrence(ServiceQualifiedNameIndex.key, it.toString())
                }
            }

            is ProtobufRpcStub -> {
                sink.occurrence(ServiceMethodIndex.key, "${stub.owner()?.qualifiedName()}/${stub.name()}")
            }
        }
    }
}
