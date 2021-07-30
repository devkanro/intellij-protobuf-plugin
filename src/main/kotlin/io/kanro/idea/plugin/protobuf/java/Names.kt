package io.kanro.idea.plugin.protobuf.java

import com.intellij.psi.util.QualifiedName
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
import io.kanro.idea.plugin.protobuf.lang.util.toQualifiedName
import io.kanro.idea.plugin.protobuf.string.toCamelCase

fun ProtobufFile.fullPackageName(): QualifiedName? {
    return javaPackage()?.toQualifiedName() ?: scope()
}

fun ProtobufFileStub.fullPackageName(): QualifiedName? {
    return javaPackage()?.toQualifiedName() ?: scope()
}

fun ProtobufFile.fullOuterClassName(): QualifiedName? {
    return if (javaMultipleFiles() == true) {
        fullPackageName()
    } else {
        fullPackageName()?.append(javaOuterClassname()) ?: QualifiedName.fromComponents(javaOuterClassname())
    }
}

fun ProtobufFileStub.fullOuterClassName(): QualifiedName? {
    return if (javaMultipleFiles() == true) {
        fullPackageName()
    } else {
        fullPackageName()?.append(javaOuterClassname()) ?: QualifiedName.fromComponents(javaOuterClassname())
    }
}

fun ProtobufMessageDefinition.className(): String? {
    return name()
}

fun ProtobufMessageStub.className(): String? {
    return name()
}

fun ProtobufMessageDefinition.messageOrBuilderName(): String? {
    return name()?.let { "${it}OrBuilder" }
}

fun ProtobufMessageStub.messageOrBuilderName(): String? {
    return name()?.let { "${it}OrBuilder" }
}

fun ProtobufMessageDefinition.fullClassName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullOuterClassName()?.append(className()) ?: QualifiedName.fromComponents(className())
        is ProtobufMessageDefinition -> owner.fullClassName()?.append(className()) ?: QualifiedName.fromComponents(
            className()
        )
        else -> null
    }
}

fun ProtobufMessageStub.fullClassName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullOuterClassName()?.append(className()) ?: QualifiedName.fromComponents(className())
        is ProtobufMessageDefinition -> owner.fullClassName()?.append(className()) ?: QualifiedName.fromComponents(
            className()
        )
        else -> null
    }
}

fun ProtobufMessageDefinition.fullBuilderName(): QualifiedName? {
    return fullClassName()?.append("Builder")
}

fun ProtobufMessageStub.fullBuilderName(): QualifiedName? {
    return fullClassName()?.append("Builder")
}

fun ProtobufMessageDefinition.fullMessageOrBuilderName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullOuterClassName()?.append(messageOrBuilderName()) ?: QualifiedName.fromComponents(
            messageOrBuilderName()
        )
        is ProtobufMessageDefinition -> owner.fullClassName()?.append(messageOrBuilderName())
            ?: QualifiedName.fromComponents(
                messageOrBuilderName()
            )
        else -> null
    }
}

fun ProtobufMessageStub.fullBuilderClassName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullOuterClassName()?.append(messageOrBuilderName()) ?: QualifiedName.fromComponents(
            messageOrBuilderName()
        )
        is ProtobufMessageDefinition -> owner.fullClassName()?.append(messageOrBuilderName())
            ?: QualifiedName.fromComponents(
                messageOrBuilderName()
            )
        else -> null
    }
}

fun ProtobufFieldLike.getterName(): String? {
    return name()?.let { "get_$it" }?.toCamelCase()
}

fun ProtobufFieldLikeStub.getterName(): String? {
    return name()?.let { "get_$it" }?.toCamelCase()
}

fun ProtobufFieldLike.setterName(): String? {
    return name()?.let { "set_$it" }?.toCamelCase()
}

fun ProtobufFieldLikeStub.setterName(): String? {
    return name()?.let { "set_$it" }?.toCamelCase()
}

fun ProtobufServiceDefinition.rpcName(): String? {
    return name()?.let { "${it}Grpc" }
}

fun ProtobufServiceStub.rpcName(): String? {
    return name()?.let { "${it}Grpc" }
}

fun ProtobufServiceDefinition.rpcKtName(): String? {
    return name()?.let { "${it}GrpcKt" }
}

fun ProtobufServiceStub.rpcKtName(): String? {
    return name()?.let { "${it}GrpcKt" }
}

fun ProtobufServiceDefinition.implBaseName(): String? {
    return name()?.let { "${it}ImplBase" }
}

fun ProtobufServiceStub.implBaseName(): String? {
    return name()?.let { "${it}ImplBase" }
}

fun ProtobufServiceDefinition.coroutineImplBaseName(): String? {
    return name()?.let { "${it}CoroutineImplBase" }
}

fun ProtobufServiceStub.coroutineImplBaseName(): String? {
    return name()?.let { "${it}CoroutineImplBase" }
}

fun ProtobufServiceDefinition.stubName(): String? {
    return name()?.let { "${it}Stub" }
}

fun ProtobufServiceStub.stubName(): String? {
    return name()?.let { "${it}Stub" }
}

fun ProtobufServiceDefinition.blockingStubName(): String? {
    return name()?.let { "${it}BlockingStub" }
}

fun ProtobufServiceStub.blockingStubName(): String? {
    return name()?.let { "${it}BlockingStub" }
}

fun ProtobufServiceDefinition.futureStubName(): String? {
    return name()?.let { "${it}FutureStub" }
}

fun ProtobufServiceStub.futureStubName(): String? {
    return name()?.let { "${it}FutureStub" }
}

fun ProtobufServiceDefinition.coroutineStubName(): String? {
    return name()?.let { "${it}CoroutineStub" }
}

fun ProtobufServiceStub.coroutineStubName(): String? {
    return name()?.let { "${it}CoroutineStub" }
}

fun ProtobufServiceDefinition.fullRpcName(): QualifiedName {
    return owner()?.fullPackageName()?.append(rpcName()) ?: QualifiedName.fromComponents(rpcName())
}

fun ProtobufServiceStub.fullRpcName(): QualifiedName {
    return owner()?.fullPackageName()?.append(rpcName()) ?: QualifiedName.fromComponents(rpcName())
}

fun ProtobufServiceDefinition.fullRpcKtName(): QualifiedName {
    return owner()?.fullPackageName()?.append(rpcKtName()) ?: QualifiedName.fromComponents(rpcKtName())
}

fun ProtobufServiceStub.fullRpcKtName(): QualifiedName {
    return owner()?.fullPackageName()?.append(rpcKtName()) ?: QualifiedName.fromComponents(rpcKtName())
}

fun ProtobufServiceDefinition.fullImplBaseName(): QualifiedName {
    return fullRpcName().append(implBaseName())
}

fun ProtobufServiceStub.fullImplBaseName(): QualifiedName {
    return fullRpcName().append(implBaseName())
}

fun ProtobufServiceDefinition.fullCoroutineImplBaseName(): QualifiedName {
    return fullRpcKtName().append(coroutineImplBaseName())
}

fun ProtobufServiceStub.fullCoroutineImplBaseName(): QualifiedName {
    return fullRpcKtName().append(coroutineImplBaseName())
}

fun ProtobufServiceDefinition.fullStubName(): QualifiedName {
    return fullRpcName().append(stubName())
}

fun ProtobufServiceStub.fullStubName(): QualifiedName {
    return fullRpcName().append(stubName())
}

fun ProtobufServiceDefinition.fullBlockingStubName(): QualifiedName {
    return fullRpcName().append(blockingStubName())
}

fun ProtobufServiceStub.fullBlockingStubName(): QualifiedName {
    return fullRpcName().append(blockingStubName())
}

fun ProtobufServiceDefinition.fullFutureStubName(): QualifiedName {
    return fullRpcName().append(futureStubName())
}

fun ProtobufServiceStub.fullFutureStubName(): QualifiedName {
    return fullRpcName().append(futureStubName())
}

fun ProtobufServiceDefinition.fullCoroutineStubName(): QualifiedName {
    return fullRpcKtName().append(coroutineStubName())
}

fun ProtobufServiceStub.fullCoroutineStubName(): QualifiedName {
    return fullRpcKtName().append(coroutineStubName())
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
        is ProtobufMessageDefinition -> owner.fullClassName()?.append(className()) ?: QualifiedName.fromComponents(
            className()
        )
        else -> null
    }
}

fun ProtobufEnumStub.fullClassName(): QualifiedName? {
    return when (val owner = owner()) {
        is ProtobufFile -> owner.fullPackageName()?.append(className()) ?: QualifiedName.fromComponents(className())
        is ProtobufMessageDefinition -> owner.fullClassName()?.append(className()) ?: QualifiedName.fromComponents(
            className()
        )
        else -> null
    }
}

fun ProtobufEnumValueDefinition.valueName(): String? {
    return name()
}

fun ProtobufEnumValueStub.valueName(): String? {
    return name()
}
