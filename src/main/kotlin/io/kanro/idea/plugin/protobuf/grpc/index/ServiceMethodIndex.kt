package io.kanro.idea.plugin.protobuf.grpc.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition

class ServiceMethodIndex : StringStubIndexExtension<ProtobufRpcDefinition>() {
    override fun getKey(): StubIndexKey<String, ProtobufRpcDefinition> {
        return Companion.key
    }

    companion object {
        val key = StubIndexKey.createIndexKey<String, ProtobufRpcDefinition>("protobuf.rpc_method")
    }
}
