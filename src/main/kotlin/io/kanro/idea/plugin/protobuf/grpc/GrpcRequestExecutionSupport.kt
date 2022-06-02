package io.kanro.idea.plugin.protobuf.grpc

import com.intellij.httpClient.execution.common.CommonClientRequest
import com.intellij.httpClient.execution.common.CommonClientResponse
import com.intellij.httpClient.execution.common.CommonClientResponseBody
import com.intellij.httpClient.execution.common.RequestContext
import com.intellij.httpClient.execution.common.RequestConverter
import com.intellij.httpClient.execution.common.RequestExecutionSupport
import com.intellij.httpClient.execution.common.RequestHandler
import com.intellij.httpClient.execution.common.RunContext
import com.intellij.httpClient.http.request.HttpRequestVariableSubstitutor
import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.psi.SmartPsiElementPointer
import io.grpc.Metadata

class GrpcRequestExecutionSupport : RequestExecutionSupport<GrpcRequest> {
    companion object {
        const val GRPC = "GRPC"
        val supportedMethod = setOf(GRPC)
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
}

data class GrpcRequest(
    val tls: Boolean, val host: String, val method: String, val metadata: Metadata, override val textToSend: String?
) : CommonClientRequest {
    override val httpMethod: String?
        get() = GrpcRequestExecutionSupport.GRPC

    override val URL: String?
        get() = "http${if (tls) "s" else ""}://$host/$method"
}

data class GrpcResponse(
    override val body: CommonClientResponseBody, override var executionTime: Long?
) : CommonClientResponse

object GrpcRequestConverter : RequestConverter<GrpcRequest>() {
    override val requestType: Class<GrpcRequest> get() = GrpcRequest::class.java

    override fun psiToCommonRequest(
        requestPsiPointer: SmartPsiElementPointer<HttpRequest>, substitutor: HttpRequestVariableSubstitutor
    ): GrpcRequest {
        val element = requestPsiPointer.element ?: throw RuntimeException("http request")
        val tls = element.requestTarget?.scheme?.text == "https"
        val host = element.getHttpHost(substitutor) ?: ""
        val method = element.getHttpUrl(substitutor) ?: ""
        val metadata = Metadata().apply {
            element.headerFieldList.forEach {
                put(Metadata.Key.of(it.name, Metadata.ASCII_STRING_MARSHALLER), it.getValue(substitutor))
            }
        }

        return GrpcRequest(
            tls, host, method, metadata, element.requestBody?.text ?: "{}"
        )
    }

    override fun toExternalFormInner(request: GrpcRequest, fileName: String?): String {
        return "${request.httpMethod} ${request.URL}"
    }
}

object GrpcRequestHandler : RequestHandler<GrpcRequest> {
    override fun execute(request: GrpcRequest, runContext: RunContext): CommonClientResponse {
        return GrpcResponse(CommonClientResponseBody.Text("Test"), 10)
    }

    override fun prepareExecutionEnvironment(request: GrpcRequest, runContext: RunContext) {
    }
}