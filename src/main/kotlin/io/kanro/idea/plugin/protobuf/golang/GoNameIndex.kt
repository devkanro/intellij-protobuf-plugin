package io.kanro.idea.plugin.protobuf.golang

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

class GoNameIndex : StringStubIndexExtension<ProtobufElement>() {
    override fun getKey(): StubIndexKey<String, ProtobufElement> {
        return GoNameIndex.key
    }

    companion object {
        val key = StubIndexKey.createIndexKey<String, ProtobufElement>("protobuf.go_name")
    }
}
