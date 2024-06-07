package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.impl.ProtobufOneofDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufOneofStub

object ProtobufOneofStubType : ProtobufStubTypeBase<ProtobufOneofStub, ProtobufOneofDefinition>(
    "ONEOF_DEFINITION",
) {
    override fun getExternalId(): String {
        return "protobuf.oneof.stub"
    }

    override fun createStub(
        data: Array<String>,
        external: Map<String, String>,
        parentStub: StubElement<*>?,
    ): ProtobufOneofStub {
        return ProtobufOneofStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufOneofStub): ProtobufOneofDefinition {
        return ProtobufOneofDefinitionImpl(stub, stub.stubType)
    }
}
