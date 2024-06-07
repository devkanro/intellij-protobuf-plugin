package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufFieldLikeStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type.ProtobufFieldStubType

class ProtobufFieldStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?,
) : ProtobufStubBase<ProtobufFieldDefinition>(data, external, parent, ProtobufFieldStubType),
    ProtobufStub<ProtobufFieldDefinition>,
    ProtobufFieldLikeStub {
    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }
}
