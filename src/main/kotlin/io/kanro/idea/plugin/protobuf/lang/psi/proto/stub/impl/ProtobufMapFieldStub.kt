package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMapFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufFieldLikeStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type.ProtobufMapFieldStubType

class ProtobufMapFieldStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?,
) : ProtobufStubBase<ProtobufMapFieldDefinition>(data, external, parent, ProtobufMapFieldStubType),
    ProtobufStub<ProtobufMapFieldDefinition>,
    ProtobufFieldLikeStub {
    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
