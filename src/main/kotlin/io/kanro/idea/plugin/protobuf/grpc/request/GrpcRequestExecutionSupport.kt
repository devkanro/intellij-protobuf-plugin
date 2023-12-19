package io.kanro.idea.plugin.protobuf.grpc.request

import com.intellij.httpClient.execution.common.RequestContext
import com.intellij.httpClient.execution.common.RequestConverter
import com.intellij.httpClient.execution.common.RequestExecutionSupport
import com.intellij.httpClient.execution.common.RequestHandler

@Suppress("UnstableApiUsage")
class GrpcRequestExecutionSupport : RequestExecutionSupport<GrpcRequest> {
    companion object {
        const val GRPC = "GRPC"
        val supportedMethod = setOf(GRPC)

        val supportedTranscodingMethod = setOf("GET", "POST", "PUT", "DELETE", "PATCH")

        val supportedSchemes = listOf("http", "https")
    }

    override fun canProcess(requestContext: RequestContext): Boolean {
        return requestContext.method in supportedMethod
    }

    override fun getRequestConverter(): RequestConverter<GrpcRequest> {
        return GrpcRequestConverter
    }

    override fun getRequestHandler(): RequestHandler<GrpcRequest> {
        return GrpcRequestHandler
    }

    override fun supportedMethods(): Collection<String> {
        return supportedMethod
    }

    override val needsScheme: Boolean get() = false

    override val supportedSchemes: List<String> get() = GrpcRequestExecutionSupport.supportedSchemes
}
