package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufVirtualScopeStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.type.ProtobufOneofStubType

class ProtobufOneofStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?
) : ProtobufStubBase<ProtobufOneofDefinition>(data, external, parent, ProtobufOneofStubType),
    ProtobufStub<ProtobufOneofDefinition>,
    ProtobufDefinitionStub,
    ProtobufVirtualScopeStub {
    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
