package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedRange
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufBodyOwner

interface ProtobufNumberScope : ProtobufScope {
    fun reservedRange(): Array<ProtobufReservedRange> {
        return if (this is ProtobufBodyOwner) {
            this.body()?.findChildren() ?: arrayOf()
        } else {
            findChildren()
        }
    }
    fun extensionRange(): Array<ProtobufReservedRange> {
        return if (this is ProtobufBodyOwner) {
            this.body()?.findChildren() ?: arrayOf()
        } else {
            findChildren()
        }
    }
    fun allowAlias(): Boolean {
        return false
    }
}
