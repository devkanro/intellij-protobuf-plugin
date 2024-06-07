package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufVirtualScope
import javax.swing.Icon

interface ProtobufOneofDefinition : ProtobufVirtualScope, ProtobufDefinition {
    override fun type(): String {
        return "oneof"
    }

    override fun getPresentableText(): String? {
        return "oneof ${name()}"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return ProtobufIcons.ONEOF
    }
}
