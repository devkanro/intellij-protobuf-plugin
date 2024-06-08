package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.support.Options

fun ProtobufOptionName.field(): ProtobufFieldLike? {
    return this.leaf().reference?.resolve() as? ProtobufFieldLike
}

fun ProtobufOptionName.isFieldDefaultOption(): Boolean {
    return this.textMatches("default") && parentOfType<ProtobufOptionOwner>() is ProtobufFieldDefinition
}

fun ProtobufOptionName.isFieldJsonNameOption(): Boolean {
    return this.textMatches("json_name") && parentOfType<ProtobufOptionOwner>() is ProtobufFieldDefinition
}

fun ProtobufOptionName.resolve(): ProtobufFieldLike? {
    extensionFieldName?.let {
        return it.reference?.resolve() as? ProtobufFieldLike
    }

    return reference?.resolve() as? ProtobufFieldLike
}

fun ProtobufOptionName.optionType(): Options? = when (parentOfType<ProtobufOptionOwner>()) {
    is ProtobufFile -> Options.FILE_OPTIONS
    is ProtobufMessageDefinition, is ProtobufGroupDefinition -> Options.MESSAGE_OPTIONS
    is ProtobufFieldDefinition, is ProtobufMapFieldDefinition -> Options.FIELD_OPTIONS
    is ProtobufOneofDefinition -> Options.ONEOF_OPTIONS
    is ProtobufEnumDefinition -> Options.ENUM_OPTIONS
    is ProtobufEnumValueDefinition -> Options.ENUM_VALUE_OPTIONS
    is ProtobufServiceDefinition -> Options.SERVICE_OPTIONS
    is ProtobufRpcDefinition -> Options.METHOD_OPTIONS
    is ProtobufExtensionRange -> Options.EXTENSION_RANGE_OPTIONS
    else -> null
}