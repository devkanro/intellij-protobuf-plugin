package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import javax.swing.Icon

interface ProtobufServiceDefinition : ProtobufScope, ProtobufDefinition {
    override fun scope(): QualifiedName? {
        return qualifiedName()
    }

    override fun type(): String {
        return "service"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.SERVICE
    }
}
