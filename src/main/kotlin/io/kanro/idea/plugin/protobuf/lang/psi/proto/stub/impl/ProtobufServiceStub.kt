package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufScopeStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type.ProtobufServiceStubType

class ProtobufServiceStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?,
) : ProtobufStubBase<ProtobufServiceDefinition>(data, external, parent, ProtobufServiceStubType),
    ProtobufStub<ProtobufServiceDefinition>,
    ProtobufDefinitionStub,
    ProtobufScopeStub {
    override fun owner(): ProtobufFileStub? {
        return parentOfType()
    }

    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }

    override fun scope(): QualifiedName? {
        return qualifiedName()
    }
}
