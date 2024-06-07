package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.impl.ProtobufGroupDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufGroupStub

object ProtobufGroupStubType : ProtobufStubTypeBase<ProtobufGroupStub, ProtobufGroupDefinition>(
    "GROUP_DEFINITION",
) {
    override fun getExternalId(): String {
        return "protobuf.group.stub"
    }

    override fun createStub(
        data: Array<String>,
        external: Map<String, String>,
        parentStub: StubElement<*>?,
    ): ProtobufGroupStub {
        return ProtobufGroupStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufGroupStub): ProtobufGroupDefinition {
        return ProtobufGroupDefinitionImpl(stub, stub.stubType)
    }
}
