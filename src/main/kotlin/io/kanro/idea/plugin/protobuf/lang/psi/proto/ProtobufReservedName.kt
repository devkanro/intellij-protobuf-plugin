package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.psi.util.elementType
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens
import io.kanro.idea.plugin.protobuf.lang.psi.value.decodeStringFromStringLiteral


fun ProtobufReservedName.name(): String {
    return if (firstChild.elementType == ProtobufTokens.STRING_LITERAL) {
        decodeStringFromStringLiteral(firstChild)
    } else {
        text
    }
}
