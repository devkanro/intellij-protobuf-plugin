package io.kanro.idea.plugin.protobuf.grpc.request

import com.bybutter.sisyphus.security.base64
import com.intellij.httpClient.execution.common.CommonClientResponse
import com.intellij.httpClient.execution.common.CommonClientResponseBody
import com.intellij.json.JsonFileType
import com.intellij.openapi.fileTypes.FileType
import io.grpc.Metadata
import io.grpc.Status

@Suppress("UnstableApiUsage")
data class GrpcResponse(
    override val body: CommonClientResponseBody,
    var header: Metadata?,
    var status: Status?,
    var trailer: Metadata?,
    override var executionTime: Long?,
) : CommonClientResponse {
    override val statusPresentation: String
        get() = status?.code?.name ?: "EXECUTING"

    override val presentationHeader: String
        get() = printMetadata(header)

    override val presentationFooter: String
        get() = printMetadata(trailer)

    private fun printMetadata(metadata: Metadata?): String {
        metadata ?: return ""

        return buildString {
            metadata.keys().forEach {
                val value =
                    if (it.endsWith("-bin")) {
                        val key = Metadata.Key.of(it, Metadata.BINARY_BYTE_MARSHALLER)
                        metadata[key]?.base64()
                    } else {
                        val key = Metadata.Key.of(it, Metadata.ASCII_STRING_MARSHALLER)
                        metadata[key]
                    }
                append(it)
                append(": ")
                appendLine(value)
            }
        }
    }

    override fun suggestFileTypeForPresentation(): FileType? {
        return JsonFileType.INSTANCE
    }
}
