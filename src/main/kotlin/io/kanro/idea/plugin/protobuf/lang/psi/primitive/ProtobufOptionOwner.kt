package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionAssign

interface ProtobufOptionOwner : ProtobufElement {
    fun options(): Array<ProtobufOptionAssign>
}
