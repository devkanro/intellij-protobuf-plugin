package io.kanro.idea.plugin.protobuf.sisyphus

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.java.javaPackage
import io.kanro.idea.plugin.protobuf.java.jsonName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufEnumStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufEnumValueStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufMessageStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufRpcStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.impl.ProtobufServiceStub
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.primitive.ProtobufFieldLikeStub
import io.kanro.idea.plugin.protobuf.lang.util.toQualifiedName
import io.kanro.idea.plugin.protobuf.string.toCamelCase
import io.kanro.idea.plugin.protobuf.string.toScreamingSnakeCase

fun ProtobufFile.fullPackageName(): QualifiedName? {
    return javaPackage()?.toQualifiedName() ?: scope()
}

fun io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufFileStub.fullPackageName(): QualifiedName? {
    return javaPackage()?.toQualifiedName() ?: scope()
}

fun ProtobufFile.fullInternalPackageName(): QualifiedName {
    return fullPackageName()?.append("internal") ?: QualifiedName.fromComponents("internal")
}

fun io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.ProtobufFileStub.fullInternalPackageName(): QualifiedName {
    return fullPackageName()?.append("internal") ?: QualifiedName.fromComponents("internal")
}

fun ProtobufMessageDefinition.className(): String? {
    return name()
}

fun ProtobufMessageStub.className(): String? {
    return name()
}

fun ProtobufMessageDefinition.mutableClassName(): String? {
    return name()?.let { "Mutable$it" }
}

fun ProtobufMessageStub.mutableClassName(): String? {
    return name()?.let { "Mutable$it" }
}

fun ProtobufMessageDefinition.fullClassName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullPackageName()?.append(className()) ?: QualifiedName.fromComponents(className())
        is ProtobufMessageDefinition ->
            owner.fullClassName()?.append(className()) ?: QualifiedName.fromComponents(
                className(),
            )

        else -> null
    }
}

fun ProtobufMessageStub.fullClassName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullPackageName()?.append(className()) ?: QualifiedName.fromComponents(className())
        is ProtobufMessageDefinition ->
            owner.fullClassName()?.append(className()) ?: QualifiedName.fromComponents(
                className(),
            )

        else -> null
    }
}

fun ProtobufMessageDefinition.fullMutableClassName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullInternalPackageName().append(mutableClassName())
        is ProtobufMessageDefinition ->
            owner.fullClassName()?.append(mutableClassName())
                ?: QualifiedName.fromComponents(mutableClassName())

        else -> null
    }
}

fun ProtobufMessageStub.fullMutableClassName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullInternalPackageName().append(mutableClassName())
        is ProtobufMessageDefinition ->
            owner.fullClassName()?.append(mutableClassName())
                ?: QualifiedName.fromComponents(mutableClassName())

        else -> null
    }
}

fun ProtobufFieldLike.propertyName(): String? {
    return jsonName() ?: name()?.toCamelCase()
}

fun ProtobufFieldLikeStub.propertyName(): String? {
    return jsonName() ?: name()?.toCamelCase()
}

fun ProtobufFieldLike.getterName(): String? {
    return propertyName()?.let { "get_$it" }?.toCamelCase()
}

fun ProtobufFieldLikeStub.getterName(): String? {
    return propertyName()?.let { "get_$it" }?.toCamelCase()
}

fun ProtobufFieldLike.setterName(): String? {
    return propertyName()?.let { "set_$it" }?.toCamelCase()
}

fun ProtobufFieldLikeStub.setterName(): String? {
    return propertyName()?.let { "set_$it" }?.toCamelCase()
}

fun ProtobufServiceDefinition.className(): String? {
    return name()
}

fun ProtobufServiceStub.className(): String? {
    return name()
}

fun ProtobufServiceDefinition.fullClassName(): QualifiedName {
    return owner()?.fullPackageName()?.append(className()) ?: QualifiedName.fromComponents(className())
}

fun ProtobufServiceStub.fullClassName(): QualifiedName {
    return owner()?.fullPackageName()?.append(className()) ?: QualifiedName.fromComponents(className())
}

fun ProtobufServiceDefinition.fullClientName(): QualifiedName {
    return fullClassName().append("Client")
}

fun ProtobufServiceStub.fullClientName(): QualifiedName {
    return fullClassName().append("Client")
}

fun ProtobufRpcDefinition.methodName(): String? {
    return name()?.toCamelCase()
}

fun ProtobufRpcStub.methodName(): String? {
    return name()?.toCamelCase()
}

fun ProtobufEnumDefinition.className(): String? {
    return name()
}

fun ProtobufEnumStub.className(): String? {
    return name()
}

fun ProtobufEnumDefinition.fullClassName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullPackageName()?.append(className()) ?: QualifiedName.fromComponents(className())
        is ProtobufMessageDefinition ->
            owner.fullClassName()?.append(className()) ?: QualifiedName.fromComponents(
                className(),
            )

        else -> null
    }
}

fun ProtobufEnumStub.fullClassName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullPackageName()?.append(className()) ?: QualifiedName.fromComponents(className())
        is ProtobufMessageDefinition ->
            owner.fullClassName()?.append(className()) ?: QualifiedName.fromComponents(
                className(),
            )

        else -> null
    }
}

fun ProtobufEnumValueDefinition.valueName(): String? {
    val enumValue = owner()?.name()?.toScreamingSnakeCase() ?: return null
    return name()?.substringAfter("${enumValue}_")
}

fun ProtobufEnumValueStub.valueName(): String? {
    val enumValue = owner()?.name()?.toScreamingSnakeCase() ?: return null
    return name()?.substringAfter("${enumValue}_")
}
