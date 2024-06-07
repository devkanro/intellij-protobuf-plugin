package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.impl.ProtobufRpcDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufRpcStub

object ProtobufRpcStubType : ProtobufStubTypeBase<ProtobufRpcStub, ProtobufRpcDefinition>(
    "RPC_DEFINITION",
) {
    override fun getExternalId(): String {
        return "protobuf.rpc.stub"
    }

    override fun createStub(
        data: Array<String>,
        external: Map<String, String>,
        parentStub: StubElement<*>?,
    ): ProtobufRpcStub {
        return ProtobufRpcStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufRpcStub): ProtobufRpcDefinition {
        return ProtobufRpcDefinitionImpl(stub, stub.stubType)
    }
}
