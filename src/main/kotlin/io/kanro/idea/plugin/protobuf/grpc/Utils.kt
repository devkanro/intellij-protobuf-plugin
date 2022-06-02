package io.kanro.idea.plugin.protobuf.grpc

import com.intellij.httpClient.http.request.psi.HttpMessageBody
import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.json.psi.JsonElement
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition

fun JsonElement.injectedRequest(): HttpRequest? {
    val host = InjectedLanguageManager.getInstance(project).getInjectionHost(this)
    if (host !is HttpMessageBody) return null
    return host.parentOfType()
}

fun HttpRequest.isGrpcRequest(): Boolean {
    return CachedValuesManager.getCachedValue(this) {
        CachedValueProvider.Result.create(
            method?.text in GrpcRequestExecutionSupport.supportedMethod,
            PsiModificationTracker.MODIFICATION_COUNT
        )
    }
}

fun HttpRequest.grpcMethod(): ProtobufRpcDefinition? {
    if (!isGrpcRequest()) return null
    return CachedValuesManager.getCachedValue(this) {
        val result = requestTarget?.pathAbsolute?.text?.trim('/')?.let {
            StubIndex.getElements(
                ServiceMethodIndex.key,
                it,
                project,
                GlobalSearchScope.allScope(project),
                ProtobufRpcDefinition::class.java
            ).firstOrNull()
        }
        CachedValueProvider.Result.create(
            result,
            PsiModificationTracker.MODIFICATION_COUNT
        )
    }
}