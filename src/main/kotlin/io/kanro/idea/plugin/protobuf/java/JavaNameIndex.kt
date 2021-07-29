package io.kanro.idea.plugin.protobuf.java

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

class JavaNameIndex : StringStubIndexExtension<ProtobufElement>() {
    override fun getKey(): StubIndexKey<String, ProtobufElement> {
        return JavaNameIndex.key
    }

    companion object {
        val key = StubIndexKey.createIndexKey<String, ProtobufElement>("protobuf.jvm_name")
    }
}
