package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.impl.ProtobufFieldDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufFieldStub

object ProtobufFieldStubType : ProtobufStubTypeBase<ProtobufFieldStub, ProtobufFieldDefinition>(
    "FIELD_DEFINITION",
) {
    override fun getExternalId(): String {
        return "protobuf.field.stub"
    }

    override fun createStub(
        data: Array<String>,
        external: Map<String, String>,
        parentStub: StubElement<*>?,
    ): ProtobufFieldStub {
        return ProtobufFieldStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufFieldStub): ProtobufFieldDefinition {
        return ProtobufFieldDefinitionImpl(stub, stub.stubType)
    }
}
