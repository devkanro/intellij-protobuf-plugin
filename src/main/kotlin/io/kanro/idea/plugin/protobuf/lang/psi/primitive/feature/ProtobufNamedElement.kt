package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufNamedElement : ProtobufElement {
    fun name(): String?
}
