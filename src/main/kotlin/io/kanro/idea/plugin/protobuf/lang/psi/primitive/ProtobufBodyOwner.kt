package io.kanro.idea.plugin.protobuf.lang.psi.primitive

interface ProtobufBodyOwner : ProtobufElement {
    fun body(): ProtobufBody?
}
