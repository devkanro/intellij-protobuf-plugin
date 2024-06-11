package io.kanro.idea.plugin.protobuf.lang.psi.proto.structure

interface ProtobufMultiNameDefinition : ProtobufDefinition {
    fun names(): Set<String>
}
