package io.kanro.idea.plugin.protobuf.lang.psi.proto

fun ProtobufTypeName.absolutely(): Boolean {
    return firstChild !is ProtobufSymbolName
}
