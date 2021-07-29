package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.impl.ProtobufServiceDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufServiceStub

object ProtobufServiceStubType : ProtobufStubTypeBase<ProtobufServiceStub, ProtobufServiceDefinition>(
    "SERVICE_DEFINITION"
) {
    override fun getExternalId(): String {
        return "protobuf.service.stub"
    }

    override fun createStub(data: Array<String>, external: Map<String, String>, parentStub: StubElement<*>?): ProtobufServiceStub {
        return ProtobufServiceStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufServiceStub): ProtobufServiceDefinition {
        return ProtobufServiceDefinitionImpl(stub, stub.stubType)
    }
}
