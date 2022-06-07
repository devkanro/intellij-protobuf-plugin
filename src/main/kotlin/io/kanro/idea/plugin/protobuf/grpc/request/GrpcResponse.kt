package io.kanro.idea.plugin.protobuf.grpc.request

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
    override var executionTime: Long?
) : CommonClientResponse {
    override val statusPresentation: String
        get() = status?.code?.name ?: "EXECUTING"

    override val presentationHeader: String
        get() = header?.let { h ->
            h.keys().joinToString("\n") {
                val value = h[Metadata.Key.of(it, Metadata.ASCII_STRING_MARSHALLER)]
                "$it: $value"
            }
        } ?: ""

    override val presentationFooter: String
        get() = trailer?.let { h ->
            h.keys().joinToString("\n") {
                val value = h[Metadata.Key.of(it, Metadata.ASCII_STRING_MARSHALLER)]
                "$it: $value"
            }
        } ?: ""

    override fun suggestFileTypeForPresentation(): FileType? {
        return JsonFileType.INSTANCE
    }
}