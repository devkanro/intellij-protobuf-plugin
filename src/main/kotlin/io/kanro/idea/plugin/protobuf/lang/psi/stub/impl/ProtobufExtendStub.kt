package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufVirtualScopeStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.type.ProtobufExtendStubType

class ProtobufExtendStub(
    data: Array<String>,
    parent: StubElement<*>?
) : ProtobufStubBase<ProtobufExtendDefinition>(data, parent, ProtobufExtendStubType),
    ProtobufStub<ProtobufExtendDefinition>,
    ProtobufVirtualScopeStub
