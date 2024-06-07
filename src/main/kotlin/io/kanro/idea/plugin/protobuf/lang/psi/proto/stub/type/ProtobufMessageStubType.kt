package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type

import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.impl.ProtobufMessageDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufMessageStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index.ResourceTypeIndex

object ProtobufMessageStubType : ProtobufStubTypeBase<ProtobufMessageStub, ProtobufMessageDefinition>(
    "MESSAGE_DEFINITION",
) {
    override fun getExternalId(): String {
        return "protobuf.message.stub"
    }

    override fun createStub(
        data: Array<String>,
        external: Map<String, String>,
        parentStub: StubElement<*>?,
    ): ProtobufMessageStub {
        return ProtobufMessageStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufMessageStub): ProtobufMessageDefinition {
        return ProtobufMessageDefinitionImpl(stub, stub.stubType)
    }

    override fun indexStub(
        stub: ProtobufMessageStub,
        sink: IndexSink,
    ) {
        super.indexStub(stub, sink)
        stub.resourceType()?.let {
            sink.occurrence(ResourceTypeIndex.key, it)
        }
    }
}
