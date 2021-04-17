package io.kanro.idea.plugin.protobuf.lang.psi.stub.impl

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.type.ProtobufPackageNameStubType

class ProtobufPackageNameStub(
    data: Array<String>,
    parent: StubElement<*>?
) : ProtobufStubBase<ProtobufPackageName>(data, parent, ProtobufPackageNameStubType),
    ProtobufStub<ProtobufPackageName>
