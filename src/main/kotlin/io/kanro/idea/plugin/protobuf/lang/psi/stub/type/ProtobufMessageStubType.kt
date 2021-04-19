package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.impl.ProtobufMessageDefinitionImpl
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMessageStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.ResourceTypeIndex

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

    override fun indexStub(stub: ProtobufMessageStub, sink: IndexSink) {
        super.indexStub(stub, sink)
        stub.resourceType()?.let {
            sink.occurrence(ResourceTypeIndex.key, it)
        }
    }
}
