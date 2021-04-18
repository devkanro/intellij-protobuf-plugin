package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.tree.IElementType

object ProtobufStubTypes {
    @JvmStatic
    fun get(name: String): IElementType {
        return when (name) {
            "MESSAGE_DEFINITION" -> ProtobufMessageStubType
            "FIELD_DEFINITION" -> ProtobufFieldStubType
            "MAP_FIELD_DEFINITION" -> ProtobufMapFieldStubType
            "ONEOF_DEFINITION" -> ProtobufOneofStubType
            "GROUP_DEFINITION" -> ProtobufGroupStubType
            "EXTEND_DEFINITION" -> ProtobufExtendStubType
            "ENUM_DEFINITION" -> ProtobufEnumStubType
            "ENUM_VALUE_DEFINITION" -> ProtobufEnumValueStubType
            "PACKAGE_NAME" -> ProtobufPackageNameStubType
            "SERVICE_DEFINITION" -> ProtobufServiceStubType
            "RPC_DEFINITION" -> ProtobufRpcStubType
            else -> throw UnsupportedOperationException("Unsupported stub type")
        }
    }
}
