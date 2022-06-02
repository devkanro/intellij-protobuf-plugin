package io.kanro.idea.plugin.protobuf.grpc

import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufIndexProvider
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufServiceStub

class ServiceMethodIndex : StringStubIndexExtension<ProtobufRpcDefinition>() {
    override fun getKey(): StubIndexKey<String, ProtobufRpcDefinition> {
        return ServiceMethodIndex.key
    }

    companion object {
        val key = StubIndexKey.createIndexKey<String, ProtobufRpcDefinition>("protobuf.rpc_method")
    }
}

class ServiceShortNameIndex : StringStubIndexExtension<ProtobufServiceDefinition>() {
    override fun getKey(): StubIndexKey<String, ProtobufServiceDefinition> {
        return ServiceShortNameIndex.key
    }

    companion object {
        val key = StubIndexKey.createIndexKey<String, ProtobufServiceDefinition>("protobuf.service_short")
    }
}

class ServiceQualifiedNameIndex : StringStubIndexExtension<ProtobufServiceDefinition>() {
    override fun getKey(): StubIndexKey<String, ProtobufServiceDefinition> {
        return ServiceQualifiedNameIndex.key
    }

    companion object {
        val key = StubIndexKey.createIndexKey<String, ProtobufServiceDefinition>("protobuf.service_qualified")
    }
}

class ServiceMethodIndexProvider : ProtobufIndexProvider {
    override fun buildIndex(stub: ProtobufStub<*>, sink: IndexSink) {
        when (stub) {
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