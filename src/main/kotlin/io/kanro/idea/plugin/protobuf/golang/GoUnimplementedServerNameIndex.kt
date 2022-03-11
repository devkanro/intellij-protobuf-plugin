package io.kanro.idea.plugin.protobuf.golang

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

class GoUnimplementedServerNameIndex : StringStubIndexExtension<ProtobufElement>() {
    override fun getKey(): StubIndexKey<String, ProtobufElement> {
        return GoUnimplementedServerNameIndex.key
    }

    companion object {
        val key = StubIndexKey.createIndexKey<String, ProtobufElement>("protobuf.go_server_name")
    }
}
