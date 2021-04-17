package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.impl.ProtobufRpcDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufRpcStub

object ProtobufRpcStubType : ProtobufStubTypeBase<ProtobufRpcStub, ProtobufRpcDefinition>(
    "RPC_DEFINITION"
) {
    override fun getExternalId(): String {
        return "protobuf.rpc.stub"
    }

    override fun createStub(data: Array<String>, parentStub: StubElement<*>?): ProtobufRpcStub {
        return ProtobufRpcStub(data, parentStub)
    }

    override fun createPsi(stub: ProtobufRpcStub): ProtobufRpcDefinition {
        return ProtobufRpcDefinitionImpl(stub, stub.stubType)
    }
}
