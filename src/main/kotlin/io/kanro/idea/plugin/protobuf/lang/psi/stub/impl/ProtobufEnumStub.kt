package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufDefinitionStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufScopeStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.type.ProtobufEnumStubType

class ProtobufEnumStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?
) : ProtobufStubBase<ProtobufEnumDefinition>(data, external, parent, ProtobufEnumStubType),
    ProtobufStub<ProtobufEnumDefinition>,
    ProtobufDefinitionStub,
    ProtobufScopeStub {
    override fun name(): String? {
        return data(0).takeIf { it.isNotEmpty() }
    }

    override fun scope(): QualifiedName? {
        return qualifiedName()
    }
}
