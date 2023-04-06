package io.kanro.idea.plugin.protobuf.lang.psi.stub.type

import com.intellij.psi.tree.IElementType

interface ProtobufStubTypes {
    companion object {
        @JvmField
        val ENUM_DEFINITION = get("ENUM_DEFINITION")

        @JvmField
        val ENUM_VALUE_DEFINITION = get("ENUM_VALUE_DEFINITION")

        @JvmField
        val EXTEND_DEFINITION = get("EXTEND_DEFINITION")

        @JvmField
        val FIELD_DEFINITION = get("FIELD_DEFINITION")

        @JvmField
        val GROUP_DEFINITION = get("GROUP_DEFINITION")

        @JvmField
        val MAP_FIELD_DEFINITION = get("MAP_FIELD_DEFINITION")

        @JvmField
        val MESSAGE_DEFINITION = get("MESSAGE_DEFINITION")

        @JvmField
        val ONEOF_DEFINITION = get("ONEOF_DEFINITION")

        @JvmField
        val PACKAGE_NAME = get("PACKAGE_NAME")

        @JvmField
        val RPC_DEFINITION = get("RPC_DEFINITION")

        @JvmField
        val SERVICE_DEFINITION = get("SERVICE_DEFINITION")

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
}
