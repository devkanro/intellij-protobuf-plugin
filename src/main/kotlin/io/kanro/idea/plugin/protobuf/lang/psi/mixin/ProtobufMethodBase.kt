package io.kanro.idea.plugin.protobuf.lang.psi.mixin

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcMethod
import io.kanro.idea.plugin.protobuf.lang.util.DocumentScope
import javax.swing.Icon

abstract class ProtobufMethodBase(node: ASTNode) : ProtobufLeafDefinitionBase(node), ProtobufRpcMethod {
    override fun type(): String {
        return "method"
    }

    override fun getIcon(unused: Boolean): Icon? {
        return Icons.SERVICE_METHOD
    }

    private fun buildTailText(): String {
        val io = rpcIOList
        if (io.size != 2) return "()"
        val input = io[0].typeName.symbolNameList.lastOrNull()?.text
        val output = io[1].typeName.symbolNameList.lastOrNull()?.text
        return "($input): $output"
    }

    override fun getPresentableText(): String? {
        return "${name()}${buildTailText()}"
    }

    override fun lookup(): LookupElementBuilder? {
        return super.lookup()?.withTailText(buildTailText(), true)
    }

    override fun navigateInfoDefinition(scope: DocumentScope) {
        scope.apply {
            definition {
                text("rpc ")
                bold(name())
                grayed(buildTailText())
            }
        }
    }
}
