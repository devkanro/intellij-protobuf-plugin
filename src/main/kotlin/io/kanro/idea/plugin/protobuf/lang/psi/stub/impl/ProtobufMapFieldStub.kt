package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.type.ProtobufMapFieldStubType

class ProtobufMapFieldStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?
) : ProtobufStubBase<ProtobufMapFieldDefinition>(data, external, parent, ProtobufMapFieldStubType),
    ProtobufStub<ProtobufMapFieldDefinition>,
    ProtobufDefinitionStub {
    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
