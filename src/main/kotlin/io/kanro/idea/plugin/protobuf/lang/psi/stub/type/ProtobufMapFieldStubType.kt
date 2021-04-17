package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.impl.ProtobufMapFieldDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMapFieldStub

object ProtobufMapFieldStubType : ProtobufStubTypeBase<ProtobufMapFieldStub, ProtobufMapFieldDefinition>(
    "MAP_FIELD_DEFINITION"
) {
    override fun getExternalId(): String {
        return "protobuf.map_field.stub"
    }

    override fun createStub(data: Array<String>, parentStub: StubElement<*>?): ProtobufMapFieldStub {
        return ProtobufMapFieldStub(data, parentStub)
    }

    override fun createPsi(stub: ProtobufMapFieldStub): ProtobufMapFieldDefinition {
        return ProtobufMapFieldDefinitionImpl(stub, stub.stubType)
    }
}
