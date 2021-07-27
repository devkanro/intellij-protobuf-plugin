package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.impl.ProtobufEnumDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufEnumStub

object ProtobufEnumStubType : ProtobufStubTypeBase<ProtobufEnumStub, ProtobufEnumDefinition>(
    "ENUM_DEFINITION"
) {
    override fun getExternalId(): String {
        return "protobuf.enum.stub"
    }

    override fun createStub(data: Array<String>, external: Map<String, String>, parentStub: StubElement<*>?): ProtobufEnumStub {
        return ProtobufEnumStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufEnumStub): ProtobufEnumDefinition {
        return ProtobufEnumDefinitionImpl(stub, stub.stubType)
    }
}
