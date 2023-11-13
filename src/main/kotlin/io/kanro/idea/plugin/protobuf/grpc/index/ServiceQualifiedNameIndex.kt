package io.kanro.idea.plugin.protobuf.grpc.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition

class ServiceQualifiedNameIndex : StringStubIndexExtension<ProtobufServiceDefinition>() {
    override fun getKey(): StubIndexKey<String, ProtobufServiceDefinition> {
        return Companion.key
    }

    companion object {
        val key = StubIndexKey.createIndexKey<String, ProtobufServiceDefinition>("protobuf.service_qualified")
    }
}
