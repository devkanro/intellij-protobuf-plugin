package io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type

import com.intellij.psi.stubs.StubElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.impl.ProtobufPackageNameImpl
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufPackageNameStub

object ProtobufPackageNameStubType : ProtobufStubTypeBase<ProtobufPackageNameStub, ProtobufPackageName>(
    "PACKAGE_NAME",
) {
    override fun getExternalId(): String {
        return "protobuf.package_name.stub"
    }

    override fun createStub(
        data: Array<String>,
        external: Map<String, String>,
        parentStub: StubElement<*>?,
    ): ProtobufPackageNameStub {
        return ProtobufPackageNameStub(data, external, parentStub)
    }

    override fun createPsi(stub: ProtobufPackageNameStub): ProtobufPackageName {
        return ProtobufPackageNameImpl(stub, stub.stubType)
    }
}
