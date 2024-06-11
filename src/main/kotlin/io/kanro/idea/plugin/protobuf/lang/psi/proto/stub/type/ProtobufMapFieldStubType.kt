package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.impl.ProtobufMapFieldDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufMapFieldStub

object ProtobufMapFieldStubType : ProtobufStubTypeBase<ProtobufMapFieldStub, ProtobufMapFieldDefinition>(
    "MAP_FIELD_DEFINITION",
) {
    override fun getExternalId(): String {
        return "protobuf.map_field.stub"
    }

    override fun createStub(
        data: Array<String>,
        external: Map<String, String>,
        parentStub: StubElement<*>?,
    ): ProtobufMapFieldStub {
        return ProtobufMapFieldStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufMapFieldStub): ProtobufMapFieldDefinition {
        return ProtobufMapFieldDefinitionImpl(stub, stub.stubType)
    }
}
