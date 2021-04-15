package io.kanro.idea.plugin.protobuf.lang.psi.primitive.element

import com.intellij.psi.util.elementType
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufRpcIO
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.findLastChild
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
        val parameters = findChildren<ProtobufRpcIO>()
        if (parameters.size != 2) return Icons.RPC_METHOD
        val inputStream = parameters[0].firstChild.elementType is ProtobufKeywordToken
        val outputStream = parameters[1].firstChild.elementType is ProtobufKeywordToken

        return when {
            inputStream && outputStream -> Icons.RPC_METHOD_BISTREAM
            outputStream -> Icons.RPC_METHOD_SERVER_STREAM
            inputStream -> Icons.RPC_METHOD_CLIENT_STREAM
            else -> Icons.RPC_METHOD
        }
    }

    @JvmDefault
    fun input(): ProtobufRpcIO? {
        return findChild()
    }

    @JvmDefault
    fun output(): ProtobufRpcIO? {
        return findLastChild()
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
