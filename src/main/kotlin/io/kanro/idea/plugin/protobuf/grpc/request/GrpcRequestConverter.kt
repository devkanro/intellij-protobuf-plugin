package io.kanro.idea.plugin.protobuf.grpc.request

import com.intellij.httpClient.execution.common.RequestConverter
import com.intellij.httpClient.http.request.HttpRequestVariableSubstitutor
import com.intellij.httpClient.http.request.psi.HttpMessageBody
import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.httpClient.http.request.psi.HttpRequestMessagesGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.psi.SmartPsiElementPointer
import io.grpc.Metadata
import io.kanro.idea.plugin.protobuf.grpc.referece.GrpcMethodReference
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stream

@Suppress("UnstableApiUsage")
object GrpcRequestConverter : RequestConverter<GrpcRequest>() {
    override val requestType: Class<GrpcRequest> get() = GrpcRequest::class.java

    override fun psiToCommonRequest(
        requestPsiPointer: SmartPsiElementPointer<HttpRequest>,
        substitutor: HttpRequestVariableSubstitutor,
    ): GrpcRequest {
        return ApplicationManager.getApplication().runReadAction<GrpcRequest> {
            if (DumbService.getInstance(requestPsiPointer.element!!.project).isDumb) {
                throw RuntimeException("Index not ready.")
            }

            val element = requestPsiPointer.element ?: throw RuntimeException("http request")
            val tls = element.requestTarget?.scheme?.text == "https"
            val host = element.getHttpHost(substitutor) ?: ""
            val method = element.requestTarget?.pathAbsolute?.getHttpPath(substitutor) ?: ""
            val metadata =
                Metadata().apply {
                    element.headerFieldList.forEach {
                        put(Metadata.Key.of(it.name, Metadata.ASCII_STRING_MARSHALLER), it.getValue(substitutor))
                    }
                }

            val requestBodyGroup =
                element.requestBody as? HttpRequestMessagesGroup
                    ?: throw IllegalStateException("Unsupported body type.")
            val requests =
                requestBodyGroup.requestMessageList.mapNotNull {
                    if (it !is HttpMessageBody) return@mapNotNull null
                    it.text
                }

            val rpc =
                element.requestTarget?.references?.filterIsInstance<GrpcMethodReference>()?.firstOrNull()
                    ?.resolve() as? ProtobufRpcDefinition ?: throw IllegalStateException("Unsolvable rpc method '$method'.")
            val input = rpc.input() ?: throw IllegalStateException("Invalid rpc input.")
            val inputMessage =
                rpc.input()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition
                    ?: throw IllegalStateException("Unsolvable rpc input '${input.typeName.text}'.")
            val output = rpc.output() ?: throw IllegalStateException("Invalid rpc input.")
            val outputMessage =
                rpc.output()?.typeName?.reference?.resolve() as? ProtobufMessageDefinition
                    ?: throw IllegalStateException("Unsolvable rpc input '${input.typeName.text}'.")

            GrpcRequest(
                tls,
                host,
                method,
                metadata,
                input.stream(),
                ".${inputMessage.qualifiedName()}",
                output.stream(),
                ".${outputMessage.qualifiedName()}",
                requests,
            )
        }
    }

    override fun toExternalFormInner(
        request: GrpcRequest,
        fileName: String?,
    ): String {
        return "${request.httpMethod} ${request.URL}"
    }
}
