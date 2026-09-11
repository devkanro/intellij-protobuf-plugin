package io.kanro.idea.plugin.protobuf.lang

import com.intellij.lang.Language

object ProtoTextLanguage : Language("protocol_buffers_text") {
    override fun getDisplayName(): String = "prototext"
}
