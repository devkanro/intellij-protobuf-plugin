package io.kanro.idea.plugin.protobuf.grpc.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition

class MessageShortNameIndex : StringStubIndexExtension<ProtobufMessageDefinition>() {
    override fun getKey(): StubIndexKey<String, ProtobufMessageDefinition> {
        return Companion.key
    }

    companion object {
        val key = StubIndexKey.createIndexKey<String, ProtobufMessageDefinition>("protobuf.message_short")
    }
}
