package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.text.StringUtil
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOneOfField
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinition
import javax.swing.Icon

abstract class ProtobufOneOfBase(node: ASTNode) : ProtobufLeafDefinitionBase(node), ProtobufOneOfField {
    override fun type(): String {
        return "oneof"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.ONEOF
    }

    override fun definitions(): Array<ProtobufDefinition> {
        return body()?.findChildren() ?: arrayOf()
    }

    override fun names(): Set<String> {
        val name = super.name() ?: return setOf()
        return setOf(StringUtil.wordsToBeginFromLowerCase(name), StringUtil.wordsToBeginFromUpperCase(name), name)
    }
}
