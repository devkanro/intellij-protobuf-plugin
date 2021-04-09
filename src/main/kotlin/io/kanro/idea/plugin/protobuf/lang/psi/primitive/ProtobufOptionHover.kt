package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionAssign

interface ProtobufOptionHover : ProtobufElement {
    fun option(): ProtobufOptionAssign?
}
