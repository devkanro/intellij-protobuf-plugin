package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type.ProtobufPackageNameStubType

class ProtobufPackageNameStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?,
) : ProtobufStubBase<ProtobufPackageName>(data, external, parent, ProtobufPackageNameStubType),
    io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub<ProtobufPackageName> {
    fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
