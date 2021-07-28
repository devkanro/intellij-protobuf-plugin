package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.type.ProtobufFieldStubType

class ProtobufFieldStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?
) : ProtobufStubBase<ProtobufFieldDefinition>(data, external, parent, ProtobufFieldStubType),
    ProtobufStub<ProtobufFieldDefinition>,
    ProtobufDefinitionStub {
    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
