package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import javax.swing.Icon

interface ProtobufServiceDefinition : ProtobufScope, ProtobufDefinition {
    override fun owner(): ProtobufFile? {
        return file()
    }

    override fun scope(): QualifiedName? {
        return qualifiedName()
    }

    override fun type(): String {
        return "service"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return ProtobufIcons.SERVICE
    }
}
