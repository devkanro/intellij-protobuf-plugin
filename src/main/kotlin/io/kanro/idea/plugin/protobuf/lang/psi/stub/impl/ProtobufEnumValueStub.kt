package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufNamedStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.type.ProtobufEnumValueStubType

class ProtobufEnumValueStub(
    data: Array<String>,
    parent: StubElement<*>?
) : ProtobufStubBase<ProtobufEnumValueDefinition>(data, parent, ProtobufEnumValueStubType),
    ProtobufStub<ProtobufEnumValueDefinition>,
    ProtobufNamedStub {
    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
