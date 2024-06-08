package io.kanro.idea.plugin.protobuf.lang.psi.proto

fun ProtobufExtensionFieldName.absolutely(): Boolean {
    return this.firstChild !is ProtobufSymbolName
}
