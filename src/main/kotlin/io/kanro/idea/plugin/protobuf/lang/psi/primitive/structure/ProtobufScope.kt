package io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedStatement
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufBodyOwner

interface ProtobufScope : ProtobufScopeItemContainer, ProtobufScopeItem {
    fun scope(): QualifiedName?

    fun reservedNames(): Array<ProtobufReservedName> {
        return if (this is ProtobufBodyOwner) {
            this.body()?.findChildren<ProtobufReservedStatement>() ?: arrayOf()
        } else {
            findChildren()
        }.flatMap {
            it.reservedNameList
        }.toTypedArray()
    }
}
