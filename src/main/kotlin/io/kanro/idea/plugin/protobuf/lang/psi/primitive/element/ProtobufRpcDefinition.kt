package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.psi.util.elementType
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcIO
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufKeywordToken
import javax.swing.Icon

interface ProtobufRpcDefinition : ProtobufDefinition {
    @JvmDefault
    override fun type(): String {
        return "rpc"
    }

    @JvmDefault
    override fun getIcon(unused: Boolean): Icon? {
        return Icons.RPC_METHOD
    }

    @JvmDefault
    override fun tailText(): String? {
        val parameters = findChildren<ProtobufRpcIO>()
        if (parameters.size != 2) return "()"
        var input = parameters[0].typeName.symbolNameList.lastOrNull()?.text ?: return "()"
        var output = parameters[1].typeName.symbolNameList.lastOrNull()?.text ?: return "()"
        if (parameters[0].firstChild.elementType is ProtobufKeywordToken) {
            input = "stream $input"
        }
        if (parameters[1].firstChild.elementType is ProtobufKeywordToken) {
            output = "stream $output"
        }
        return "($input): $output"
    }
}
