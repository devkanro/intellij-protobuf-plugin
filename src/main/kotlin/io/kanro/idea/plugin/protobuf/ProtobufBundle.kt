package io.kanro.idea.plugin.protobuf

import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey

const val PROTOBUF_BUNDLE = "io.kanro.idea.plugin.protobuf"

object ProtobufBundle : AbstractBundle(PROTOBUF_BUNDLE) {
    fun message(
        @PropertyKey(resourceBundle = PROTOBUF_BUNDLE) key: String,
        vararg params: Any,
    ) = getMessage(key, *params)

    fun messagePointer(
        @PropertyKey(resourceBundle = PROTOBUF_BUNDLE) key: String,
        vararg params: Any,
    ) = getLazyMessage(key, *params)
}
