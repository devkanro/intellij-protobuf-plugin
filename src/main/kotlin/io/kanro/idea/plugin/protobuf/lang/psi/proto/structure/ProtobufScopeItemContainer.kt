package io.kanro.idea.plugin.protobuf.lang.psi.proto.structure

import io.kanro.idea.plugin.protobuf.lang.psi.feature.BodyOwner
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement

interface ProtobufScopeItemContainer : ProtobufElement {
    fun items(): Array<ProtobufScopeItem> {
        return if (this is BodyOwner) {
            this.body()?.findChildren() ?: arrayOf()
        } else {
            findChildren()
        }
    }
}
