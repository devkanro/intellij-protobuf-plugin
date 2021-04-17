package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.impl.ProtobufMessageDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMessageStub

object ProtobufMessageStubType : ProtobufStubTypeBase<ProtobufMessageStub, ProtobufMessageDefinition>(
    "MESSAGE_DEFINITION"
) {
    override fun getExternalId(): String {
        return "protobuf.message.stub"
    }

    override fun createStub(data: Array<String>, parentStub: StubElement<*>?): ProtobufMessageStub {
        return ProtobufMessageStub(data, parentStub)
    }

    override fun createPsi(stub: ProtobufMessageStub): ProtobufMessageDefinition {
        return ProtobufMessageDefinitionImpl(stub, stub.stubType)
    }
}
