package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.impl.ProtobufEnumValueDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufEnumValueStub

object ProtobufEnumValueStubType : ProtobufStubTypeBase<ProtobufEnumValueStub, ProtobufEnumValueDefinition>(
    "ENUM_VALUE_DEFINITION"
) {
    override fun getExternalId(): String {
        return "protobuf.enum_value.stub"
    }

    override fun createStub(
        data: Array<String>,
        external: Map<String, String>,
        parentStub: StubElement<*>?
    ): ProtobufEnumValueStub {
        return ProtobufEnumValueStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufEnumValueStub): ProtobufEnumValueDefinition {
        return ProtobufEnumValueDefinitionImpl(stub, stub.stubType)
    }
}
