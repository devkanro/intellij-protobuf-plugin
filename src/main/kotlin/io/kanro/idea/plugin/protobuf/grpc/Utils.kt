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
import io.kanro.idea.plugin.protobuf.grpc.index.ServiceMethodIndex
import io.kanro.idea.plugin.protobuf.grpc.request.GrpcRequestExecutionSupport
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition

fun JsonElement.injectedRequest(): HttpRequest? {
    val host = InjectedLanguageManager.getInstance(project).getInjectionHost(this)
    if (host !is HttpMessageBody) return null
    return host.parentOfType()
}

fun HttpRequest.isTranscoding(): Boolean {
    return CachedValuesManager.getCachedValue(this) {
        CachedValueProvider.Result.create(
            method?.text in GrpcRequestExecutionSupport.supportedTranscodingMethod && getHeaderField("grpc-method") != null,
            PsiModificationTracker.MODIFICATION_COUNT,
        )
    }
}

fun HttpRequest.isNativeGrpc(): Boolean {
    return CachedValuesManager.getCachedValue(this) {
        CachedValueProvider.Result.create(
            method?.text in GrpcRequestExecutionSupport.supportedMethod,
            PsiModificationTracker.MODIFICATION_COUNT,
        )
    }
}

fun HttpRequest.isGrpcRequest(): Boolean {
    return isNativeGrpc() || isTranscoding()
}

fun HttpRequest.resolveRpc(): ProtobufRpcDefinition? {
    if (!isGrpcRequest()) return null
    return CachedValuesManager.getCachedValue(this) {
        val path =
            if (method?.text in GrpcRequestExecutionSupport.supportedMethod) {
                requestTarget?.pathAbsolute?.text?.trim('/')
            } else {
                getHeaderField("grpc-method")?.headerFieldValue?.text?.trim('/', ' ')
            }

        val result =
            path?.let {
                StubIndex.getElements(
                    ServiceMethodIndex.key,
                    it,
                    project,
                    GlobalSearchScope.allScope(project),
                    ProtobufRpcDefinition::class.java,
                ).firstOrNull()
            }
        CachedValueProvider.Result.create(
            result,
            PsiModificationTracker.MODIFICATION_COUNT,
        )
    }
}
