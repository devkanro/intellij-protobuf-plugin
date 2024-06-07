package io.kanro.idea.plugin.protobuf.lang.psi.proto.structure

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.feature.BodyOwner
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufReservedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufReservedStatement

interface ProtobufScope : ProtobufScopeItemContainer, ProtobufScopeItem {
    fun scope(): QualifiedName?

    fun reservedNames(): Array<ProtobufReservedName> {
        return if (this is BodyOwner) {
            this.body()?.findChildren<ProtobufReservedStatement>() ?: arrayOf()
        } else {
            findChildren()
        }.flatMap {
            it.reservedNameList
        }.toTypedArray()
    }
}
