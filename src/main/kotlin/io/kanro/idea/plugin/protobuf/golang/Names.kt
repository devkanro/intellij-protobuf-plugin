package io.kanro.idea.plugin.protobuf.golang

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.stub.ProtobufFileStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufEnumStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufEnumValueStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufMessageStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufServiceStub
import io.kanro.idea.plugin.protobuf.lang.psi.stub.primitive.ProtobufFieldLikeStub
import io.kanro.idea.plugin.protobuf.string.toPascalCase

fun ProtobufMessageDefinition.structName(): String? {
    return when (val owner = owner()) {
        is ProtobufFile -> name()
        is ProtobufMessageDefinition -> "${owner.structName()}_${name()}"
        else -> null
    }
}

fun ProtobufMessageStub.structName(): String? {
    return when (val owner = owner()) {
        is ProtobufFileStub -> name()
        is ProtobufMessageStub -> "${owner.structName()}_${name()}"
        else -> null
    }
}

fun ProtobufFieldLike.fieldName(): String? {
    return name()?.toPascalCase()
}

fun ProtobufFieldLikeStub.fieldName(): String? {
    return name()?.toPascalCase()
}

fun ProtobufServiceDefinition.serverName(): String? {
    return name()?.let { "${it}Server" }
}

fun ProtobufServiceStub.serverName(): String? {
    return name()?.let { "${it}Server" }
}

fun ProtobufServiceDefinition.unimplementedName(): String? {
    return name()?.let { "Unimplemented${it}Server" }
}

fun ProtobufServiceStub.unimplementedName(): String? {
    return name()?.let { "Unimplemented${it}Server" }
}

fun ProtobufServiceDefinition.clientName(): String? {
    return name()?.let { "${it}Client" }
}

fun ProtobufServiceStub.clientName(): String? {
    return name()?.let { "${it}Client" }
}

fun ProtobufRpcDefinition.funcName(): String? {
    return name()
}

fun ProtobufRpcStub.funcName(): String? {
    return name()
}

fun ProtobufEnumDefinition.typeName(): String? {
    return when (val owner = owner()) {
        is ProtobufFile -> name()
        is ProtobufMessageDefinition -> "${owner.structName()}_${name()}"
        else -> null
    }
}

fun ProtobufEnumStub.typeName(): String? {
    return when (val owner = owner()) {
        is ProtobufFileStub -> name()
        is ProtobufMessageStub -> "${owner.structName()}_${name()}"
        else -> null
    }
}

fun ProtobufEnumValueDefinition.fieldName(): String? {
    val enum = owner() ?: return null
    val parent = when (val owner = enum.owner()) {
        is ProtobufFile -> enum.name()
        is ProtobufMessageDefinition -> owner.structName()
        else -> null
    }
    return "${parent}_${name()}"
}

fun ProtobufEnumValueStub.fieldName(): String? {
    val enum = owner() ?: return null
    val parent = when (val owner = enum.owner()) {
        is ProtobufFileStub -> enum.name()
        is ProtobufMessageStub -> owner.structName()
        else -> null
    }
    return "${parent}_${name()}"
}
