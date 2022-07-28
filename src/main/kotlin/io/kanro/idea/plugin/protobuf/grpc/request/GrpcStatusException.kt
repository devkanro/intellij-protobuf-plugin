package io.kanro.idea.plugin.protobuf.grpc.request

import com.bybutter.sisyphus.rpc.DebugInfo
import io.grpc.Status

class GrpcStatusException(private val status: Status, private val details: List<Any>?) :
    Exception(buildMessage(status, details)) {
    companion object {
        private fun buildMessage(status: Status, details: List<Any>?) = buildString {
            if (status.description == null) {
                appendLine(status.code.name)
            } else {
                appendLine("${status.code.name}: ${status.description}")
            }
            details?.forEach {
                when (it) {
                    is DebugInfo -> {
                        appendLine("DebugInfo: ${it.detail}")
                        it.stackEntries.forEach {
                            appendLine("\t$it")
                        }
                    }
                }
            }
        }
    }
}