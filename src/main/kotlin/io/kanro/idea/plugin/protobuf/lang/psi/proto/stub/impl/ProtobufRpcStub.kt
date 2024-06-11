package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type.ProtobufRpcStubType

class ProtobufRpcStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?,
) : ProtobufStubBase<ProtobufRpcDefinition>(data, external, parent, ProtobufRpcStubType),
    ProtobufStub<ProtobufRpcDefinition>,
    ProtobufDefinitionStub {
    override fun owner(): ProtobufServiceStub? {
        return parentOfType()
    }

    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
