package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufIntegerValue
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.string.parseLongOrNull

interface ProtobufNumbered : ProtobufDefinition {
    fun number(): Long? {
        return intValue()?.text?.parseLongOrNull()
    }

    fun intValue(): ProtobufIntegerValue? {
        return findChild()
    }
}
