package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtensionStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedRange
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedStatement
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufBodyOwner

interface ProtobufNumberScope : ProtobufScope {
    fun reservedRange(): Array<ProtobufReservedRange> {
        return if (this is ProtobufBodyOwner) {
            this.body()?.findChildren<ProtobufReservedStatement>() ?: arrayOf()
        } else {
            findChildren()
        }.flatMap {
            it.reservedRangeList
        }.toTypedArray()
    }

    fun extensionRange(): Array<ProtobufReservedRange> {
        return if (this is ProtobufBodyOwner) {
            this.body()?.findChildren<ProtobufExtensionStatement>() ?: arrayOf()
        } else {
            findChildren()
        }.flatMap {
            it.reservedRangeList
        }.toTypedArray()
    }

    fun allowAlias(): Boolean {
        return false
    }
}
