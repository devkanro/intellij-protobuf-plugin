package io.kanro.idea.plugin.protobuf.lang.psi.primitive

interface ProtobufIdentifier : ProtobufElement {
    fun owner(): ProtobufDefinition
}
