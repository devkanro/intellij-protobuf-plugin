package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import javax.swing.Icon

interface ProtobufFieldDefinition : ProtobufFieldLike {
    @JvmDefault
    override fun type(): String {
        return "field"
    }

    @JvmDefault
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.FIELD
    }

    @JvmDefault
    override fun fieldType(): String? {
        return findChild<ProtobufTypeName>()?.symbolNameList?.lastOrNull()?.text
    }
}
