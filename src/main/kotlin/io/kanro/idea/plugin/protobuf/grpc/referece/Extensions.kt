package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.httpClient.http.request.psi.HttpQueryParameterKey
import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.aip.resolveInput
import io.kanro.idea.plugin.protobuf.grpc.injectedRequest
import io.kanro.idea.plugin.protobuf.grpc.isTranscoding
import io.kanro.idea.plugin.protobuf.grpc.resolveRpc
import io.kanro.idea.plugin.protobuf.lang.psi.nullCachedValue
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.resolveField
import io.kanro.idea.plugin.protobuf.lang.psi.proto.resolveFieldType

internal fun JsonProperty.contextJsonObject(): JsonObject? {
    return CachedValuesManager.getCachedValue(this) {
        var obj = this.parent as? JsonObject ?: return@getCachedValue nullCachedValue()
        while (true) {
            obj.findProperty("@type")?.let {
                return@getCachedValue CachedValueProvider.Result.create(obj, PsiModificationTracker.MODIFICATION_COUNT)
            }
            obj = obj.parentOfType() ?: break
        }
        null
    }
}

internal fun HttpRequest.grpcBodyType(): ProtobufMessageDefinition? {
    return CachedValuesManager.getCachedValue(this) {
        val rpcDefinition = resolveRpc() ?: return@getCachedValue nullCachedValue()
        val input = rpcDefinition.resolveInput() ?: return@getCachedValue nullCachedValue()

        val body =
            if (isTranscoding()) {
                val option =
                    rpcDefinition.options(AipOptions.httpOption).lastOrNull()
                        ?: return@getCachedValue nullCachedValue()
                val body =
                    option.value(AipOptions.httpRuleBodyField)?.toString() ?: return@getCachedValue nullCachedValue()
                if (body != "*") {
                    input.resolveFieldType(QualifiedName.fromDottedString(body), true) as? ProtobufMessageDefinition
                } else {
                    input
                }
            } else {
                input
            }

        return@getCachedValue CachedValueProvider.Result.create(body, PsiModificationTracker.MODIFICATION_COUNT)
    }
}

internal fun JsonProperty.contextMessage(): ProtobufMessageDefinition? {
    return CachedValuesManager.getCachedValue(this) {
        val contextJsonObject = contextJsonObject()
        if (contextJsonObject == null) {
            val request = injectedRequest() ?: return@getCachedValue nullCachedValue()
            val rpcDefinition = request.grpcBodyType() ?: return@getCachedValue nullCachedValue()

            return@getCachedValue CachedValueProvider.Result.create(
                rpcDefinition,
                PsiModificationTracker.MODIFICATION_COUNT,
            )
        }
        val typeUrl =
            contextJsonObject.findProperty("@type")?.value as? JsonStringLiteral
                ?: return@getCachedValue nullCachedValue()
        val input =
            typeUrl.references.firstOrNull { it is GrpcTypeUrlReference }?.resolve() as? ProtobufMessageDefinition
                ?: return@getCachedValue nullCachedValue()
        return@getCachedValue CachedValueProvider.Result.create(input, PsiModificationTracker.MODIFICATION_COUNT)
    }
}

internal fun JsonProperty.qualifiedName(): QualifiedName? {
    return CachedValuesManager.getCachedValue(this) {
        val contextObj = contextJsonObject()
        var e = this
        val q = mutableListOf<String>()
        while (true) {
            q += e.name
            if (e.parent == contextObj) break
            e = e.parentOfType() ?: break
        }
        q.reverse()

        CachedValueProvider.Result.create(
            QualifiedName.fromComponents(q),
            PsiModificationTracker.MODIFICATION_COUNT,
        )
    }
}

internal fun JsonProperty.resolveParentType(): PsiElement? {
    return CachedValuesManager.getCachedValue(this) {
        val contextMessage = contextMessage() ?: return@getCachedValue nullCachedValue()
        val qualifiedName = qualifiedName() ?: return@getCachedValue nullCachedValue()
        CachedValueProvider.Result.create(
            contextMessage.resolveFieldType(qualifiedName.removeTail(1), true),
            PsiModificationTracker.MODIFICATION_COUNT,
        )
    }
}

internal fun JsonProperty.resolve(): PsiElement? {
    return CachedValuesManager.getCachedValue(this) {
        val contextMessage = contextMessage() ?: return@getCachedValue nullCachedValue()
        val qualifiedName = qualifiedName() ?: return@getCachedValue nullCachedValue()
        CachedValueProvider.Result.create(
            contextMessage.resolveField(qualifiedName, true),
            PsiModificationTracker.MODIFICATION_COUNT,
        )
    }
}

internal fun HttpQueryParameterKey.resolve(): PsiElement? {
    return CachedValuesManager.getCachedValue(this) {
        val request = parentOfType<HttpRequest>() ?: return@getCachedValue nullCachedValue()
        val rpc = request.resolveRpc() ?: return@getCachedValue nullCachedValue()
        val input = rpc.resolveInput() ?: return@getCachedValue nullCachedValue()
        val field =
            input.resolveField(QualifiedName.fromDottedString(this.text), true)
                ?: return@getCachedValue nullCachedValue()
        CachedValueProvider.Result.create(field, PsiModificationTracker.MODIFICATION_COUNT)
    }
}
