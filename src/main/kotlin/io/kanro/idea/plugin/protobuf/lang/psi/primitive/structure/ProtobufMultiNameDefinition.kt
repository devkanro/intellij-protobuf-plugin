package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

interface ProtobufMultiNameDefinition : ProtobufDefinition {
    fun names(): Set<String>
}
