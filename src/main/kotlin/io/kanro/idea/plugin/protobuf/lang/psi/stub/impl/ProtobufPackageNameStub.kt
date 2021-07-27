package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.type.ProtobufPackageNameStubType

class ProtobufPackageNameStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?
) : ProtobufStubBase<ProtobufPackageName>(data, external, parent, ProtobufPackageNameStubType),
    ProtobufStub<ProtobufPackageName> {
    fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
