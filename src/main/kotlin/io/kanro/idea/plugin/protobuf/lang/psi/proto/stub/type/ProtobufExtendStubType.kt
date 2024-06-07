package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.impl.ProtobufExtendDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufExtendStub

object ProtobufExtendStubType : ProtobufStubTypeBase<ProtobufExtendStub, ProtobufExtendDefinition>(
    "EXTEND_DEFINITION",
) {
    override fun getExternalId(): String {
        return "protobuf.extend.stub"
    }

    override fun createStub(
        data: Array<String>,
        external: Map<String, String>,
        parentStub: StubElement<*>?,
    ): ProtobufExtendStub {
        return ProtobufExtendStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufExtendStub): ProtobufExtendDefinition {
        return ProtobufExtendDefinitionImpl(stub, stub.stubType)
    }
}
