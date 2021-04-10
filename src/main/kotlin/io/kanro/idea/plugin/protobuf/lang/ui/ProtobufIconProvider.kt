package io.kanro.idea.plugin.protobuf.lang.ui

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumValueDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufExtendDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFieldDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufGroupDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOneofDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import javax.swing.Icon

class ProtobufIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if (element !is ProtobufElement) return null
        return when (element) {
            is ProtobufFile -> Icons.FILE
            is ProtobufMessageDefinition -> Icons.MESSAGE
            is ProtobufFieldDefinition -> Icons.FIELD
            is ProtobufOneofDefinition -> Icons.ONEOF
            is ProtobufGroupDefinition -> Icons.GROUP_FIELD
            is ProtobufExtendDefinition -> Icons.EXTEND
            is ProtobufEnumDefinition -> Icons.ENUM
            is ProtobufEnumValueDefinition -> Icons.ENUM_VALUE
            is ProtobufServiceDefinition -> Icons.SERVICE
            is ProtobufRpcDefinition -> Icons.RPC_METHOD
            is ProtobufPackageName -> Icons.PACKAGE
            else -> null
        }
    }
}
