package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufIntegerValue
import io.kanro.idea.plugin.protobuf.lang.psi.findChild

interface ProtobufNumbered : ProtobufDefinition {
    @JvmDefault
    fun number(): Long? {
        return intValue()?.text?.toLong()
    }

    @JvmDefault
    fun intValue(): ProtobufIntegerValue? {
        return findChild()
    }
}
