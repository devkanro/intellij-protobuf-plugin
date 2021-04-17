package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.tree.IElementType

object ProtobufStubTypes {
    private val types = listOf(
        ProtobufMessageStubType,
        ProtobufFieldStubType,
        ProtobufMapFieldStubType,
        ProtobufOneofStubType,
        ProtobufGroupStubType,
        ProtobufExtendStubType,
        ProtobufEnumStubType,
        ProtobufEnumValueStubType,
        ProtobufPackageNameStubType,
        ProtobufServiceStubType,
        ProtobufRpcStubType
    ).associateBy { it.debugName }

    @JvmStatic
    fun get(name: String): IElementType {
        return types[name] ?: throw UnsupportedOperationException("Unsupported stub type")
    }
}
