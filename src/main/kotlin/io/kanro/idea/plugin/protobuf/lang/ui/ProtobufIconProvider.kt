package io.kanro.idea.plugin.protobuf.lang.ui

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufServiceDefinition
import javax.swing.Icon

class ProtobufIconProvider : IconProvider() {
    override fun getIcon(
        element: PsiElement,
        flags: Int,
    ): Icon? {
        if (element !is ProtobufElement) return null
        return when (element) {
            is ProtobufFile -> ProtobufIcons.FILE
            is ProtobufMessageDefinition -> ProtobufIcons.MESSAGE
            is ProtobufFieldDefinition -> ProtobufIcons.FIELD
            is ProtobufOneofDefinition -> ProtobufIcons.ONEOF
            is ProtobufGroupDefinition -> ProtobufIcons.GROUP_FIELD
            is ProtobufExtendDefinition -> ProtobufIcons.EXTEND
            is ProtobufEnumDefinition -> ProtobufIcons.ENUM
            is ProtobufEnumValueDefinition -> ProtobufIcons.ENUM_VALUE
            is ProtobufServiceDefinition -> ProtobufIcons.SERVICE
            is ProtobufRpcDefinition -> ProtobufIcons.RPC_METHOD
            is ProtobufPackageName -> ProtobufIcons.PACKAGE
            else -> null
        }
    }
}
