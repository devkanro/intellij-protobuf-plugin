package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.impl.ProtobufGroupDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufGroupStub

object ProtobufGroupStubType : ProtobufStubTypeBase<ProtobufGroupStub, ProtobufGroupDefinition>(
    "GROUP_DEFINITION"
) {
    override fun getExternalId(): String {
        return "protobuf.group.stub"
    }

    override fun createStub(data: Array<String>, parentStub: StubElement<*>?): ProtobufGroupStub {
        return ProtobufGroupStub(data, parentStub)
    }

    override fun createPsi(stub: ProtobufGroupStub): ProtobufGroupDefinition {
        return ProtobufGroupDefinitionImpl(stub, stub.stubType)
    }
}
