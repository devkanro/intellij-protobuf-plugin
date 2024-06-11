package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement

class ShortNameIndex : StringStubIndexExtension<ProtobufElement>() {
    override fun getKey(): StubIndexKey<String, ProtobufElement> {
        return ShortNameIndex.key
    }

    companion object {
        val key = StubIndexKey.createIndexKey<String, ProtobufElement>("protobuf.short_name")
    }
}
