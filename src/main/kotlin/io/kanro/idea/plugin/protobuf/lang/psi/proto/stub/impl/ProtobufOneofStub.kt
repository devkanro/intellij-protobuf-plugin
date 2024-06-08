package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufVirtualScopeStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type.ProtobufOneofStubType

class ProtobufOneofStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?,
) : ProtobufStubBase<ProtobufOneofDefinition>(data, external, parent, ProtobufOneofStubType),
    io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub<ProtobufOneofDefinition>,
    ProtobufDefinitionStub,
    ProtobufVirtualScopeStub {
    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
