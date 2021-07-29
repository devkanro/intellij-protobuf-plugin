package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.stub.type.ProtobufEnumValueStubType

class ProtobufEnumValueStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?
) : ProtobufStubBase<ProtobufEnumValueDefinition>(data, external, parent, ProtobufEnumValueStubType),
    ProtobufStub<ProtobufEnumValueDefinition>,
    ProtobufDefinitionStub {
    override fun owner(): ProtobufEnumStub? {
        return parentOfType()
    }

    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
