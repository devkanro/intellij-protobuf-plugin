package io.kanro.idea.plugin.protobuf.grpc

import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.httpClient.injection.http.request.ImplicitMimeTypeProvider

class GrpcMimeTypeProvider : ImplicitMimeTypeProvider {
    override fun getRequestPayloadMimeType(request: HttpRequest): String? {
        if (request.httpMethod !in GrpcRequestExecutionSupport.supportedMethod) return null
        return "application/json"
    }
}