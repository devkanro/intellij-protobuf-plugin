package io.kanro.idea.plugin.protobuf.lang.psi.primitive

interface ProtobufDefinitionContributor : ProtobufElement {
    fun definitions(): Array<ProtobufDefinition>
}
