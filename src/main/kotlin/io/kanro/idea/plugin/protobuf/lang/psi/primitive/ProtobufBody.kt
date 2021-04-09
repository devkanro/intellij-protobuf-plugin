package io.kanro.idea.plugin.protobuf.lang.psi.primitive

interface ProtobufBody : ProtobufElement {
    fun owner(): ProtobufBodyOwner
}
