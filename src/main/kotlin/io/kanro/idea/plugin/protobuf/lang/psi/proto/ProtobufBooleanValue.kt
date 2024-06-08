package io.kanro.idea.plugin.protobuf.lang.psi.proto

fun ProtobufBooleanValue.value(): Boolean {
    return textMatches("true")
}
