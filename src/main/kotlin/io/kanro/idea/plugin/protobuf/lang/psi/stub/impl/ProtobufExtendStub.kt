package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufVirtualScopeStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.type.ProtobufExtendStubType

class ProtobufExtendStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?
) : ProtobufStubBase<ProtobufExtendDefinition>(data, external, parent, ProtobufExtendStubType),
    ProtobufStub<ProtobufExtendDefinition>,
    ProtobufVirtualScopeStub
