package io.kanro.idea.plugin.protobuf.grpc.request

import com.intellij.httpClient.execution.common.CommonClientRequest
import io.grpc.Metadata

@Suppress("UnstableApiUsage")
data class GrpcRequest(
    val tls: Boolean,
    val host: String,
    val method: String,
    val metadata: Metadata,
    val inputStreaming: Boolean,
    val inputType: String,
    val outputStreaming: Boolean,
    val outputType: String,
    val requests: List<String>
) : CommonClientRequest {
    override val httpMethod: String
        get() = GrpcRequestExecutionSupport.GRPC

    override val URL: String
        get() = "http${if (tls) "s" else ""}://$host/$method"

    override val textToSend: String
        get() = requests.joinToString { "\n\n" }
}