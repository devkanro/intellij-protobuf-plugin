package io.kanro.idea.plugin.protobuf.lang.psi.proto

import io.kanro.idea.plugin.protobuf.string.parseDoubleOrNull
import io.kanro.idea.plugin.protobuf.string.parseLongOrNull


fun ProtobufNumberValue.float(): Double? {
    return floatLiteral?.text?.parseDoubleOrNull()
        ?: integerLiteral?.text?.parseLongOrNull()?.toDouble()
}

fun ProtobufNumberValue.int(): Long? {
    return integerLiteral?.text?.parseLongOrNull()
}

fun ProtobufNumberValue.uint(): Long? {
    return int()
}
