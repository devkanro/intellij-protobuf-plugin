package io.kanro.idea.plugin.protobuf.grpc.referece

import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.grpc.grpcMethod
import io.kanro.idea.plugin.protobuf.grpc.injectedRequest
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.resolveField
import io.kanro.idea.plugin.protobuf.lang.psi.resolveFieldType

internal fun JsonProperty.contextJsonObject(): JsonObject? {
    return CachedValuesManager.getCachedValue(this) {
        var obj = this.parent as? JsonObject ?: return@getCachedValue null
        while (true) {
            obj.findProperty("@type")?.let {
                return@getCachedValue CachedValueProvider.Result.create(obj, PsiModificationTracker.MODIFICATION_COUNT)
            }
            obj = obj.parentOfType() ?: break
        }
        null
    }
}

internal fun JsonProperty.contextMessage(): ProtobufMessageDefinition? {
    return CachedValuesManager.getCachedValue(this) {
        val contextJsonObject = contextJsonObject()
        if (contextJsonObject == null) {
            val request = injectedRequest() ?: return@getCachedValue null
            val rpcDefinition = request.grpcMethod() ?: return@getCachedValue null
            val input =
                rpcDefinition.rpcIOList.firstOrNull()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition
                    ?: return@getCachedValue null
            return@getCachedValue CachedValueProvider.Result.create(input, PsiModificationTracker.MODIFICATION_COUNT)
        }
        val typeUrl = contextJsonObject.findProperty("@type")?.value as? JsonStringLiteral
            ?: return@getCachedValue null
        val input =
            typeUrl.references.firstOrNull { it is GrpcTypeUrlReference }?.resolve() as? ProtobufMessageDefinition
                ?: return@getCachedValue null
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
            QualifiedName.fromComponents(q), PsiModificationTracker.MODIFICATION_COUNT
        )
    }
}

internal fun JsonProperty.resolveParentType(): PsiElement? {
    return CachedValuesManager.getCachedValue(this) {
        val contextMessage = contextMessage() ?: return@getCachedValue null
        val qualifiedName = qualifiedName() ?: return@getCachedValue null
        CachedValueProvider.Result.create(
            contextMessage.resolveFieldType(qualifiedName.removeTail(1)), PsiModificationTracker.MODIFICATION_COUNT
        )
    }
}

internal fun JsonProperty.resolve(): PsiElement? {
    return CachedValuesManager.getCachedValue(this) {
        val contextMessage = contextMessage() ?: return@getCachedValue null
        val qualifiedName = qualifiedName() ?: return@getCachedValue null
        CachedValueProvider.Result.create(
            contextMessage.resolveField(qualifiedName), PsiModificationTracker.MODIFICATION_COUNT
        )
    }
}
