package io.kanro.idea.plugin.protobuf.lang

import com.intellij.lang.Language

object ProtobufLanguage : Language("protocol_buffers") {
    override fun getDisplayName(): String = "protobuf"
}
