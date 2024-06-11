package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufNumberScope
import javax.swing.Icon

interface ProtobufEnumDefinition : ProtobufNumberScope, ProtobufDefinition, ProtobufOptionOwner {
    override fun scope(): QualifiedName? {
        return qualifiedName()
    }

    override fun type(): String {
        return "enum"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return ProtobufIcons.ENUM
    }

    override fun allowAlias(): Boolean {
        return this.options("allow_alias").firstOrNull()?.value() == true
    }
}
