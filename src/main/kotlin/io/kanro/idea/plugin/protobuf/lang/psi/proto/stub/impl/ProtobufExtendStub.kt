package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufVirtualScopeStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type.ProtobufExtendStubType

class ProtobufExtendStub(
    data: Array<String>,
    external: Map<String, String>,
    parent: StubElement<*>?,
) : ProtobufStubBase<ProtobufExtendDefinition>(data, external, parent, ProtobufExtendStubType),
    ProtobufStub<ProtobufExtendDefinition>,
    ProtobufVirtualScopeStub
