package io.kanro.idea.plugin.protobuf.lang.psi.proto.structure

import io.kanro.idea.plugin.protobuf.lang.psi.feature.BodyOwner
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtensionRange
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtensionStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufReservedRange
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufReservedStatement

interface ProtobufNumberScope : ProtobufScope {
    fun reservedRange(): Array<ProtobufReservedRange> {
        return if (this is BodyOwner) {
            this.body()?.findChildren<ProtobufReservedStatement>() ?: arrayOf()
        } else {
            findChildren()
        }.flatMap {
            it.reservedRangeList
        }.toTypedArray()
    }

    fun extensionRange(): Array<ProtobufExtensionRange> {
        return if (this is BodyOwner) {
            this.body()?.findChildren<ProtobufExtensionStatement>() ?: arrayOf()
        } else {
            findChildren()
        }.flatMap {
            it.extensionRangeList
        }.toTypedArray()
    }

    fun allowAlias(): Boolean {
        return false
    }
}
