package io.kanro.idea.plugin.protobuf.lang.psi.proto.element

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.findLastChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufRpcIO
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stream
import javax.swing.Icon

interface ProtobufRpcDefinition : ProtobufDefinition {
    override fun owner(): ProtobufServiceDefinition? {
        return parentOfType()
    }

    override fun type(): String {
        return "rpc"
    }

    override fun getIcon(unused: Boolean): Icon? {
        val parameters = findChildren<ProtobufRpcIO>()
        if (parameters.size != 2) return ProtobufIcons.RPC_METHOD
        val inputStream = parameters[0].stream()
        val outputStream = parameters[1].stream()

        return when {
            inputStream && outputStream -> ProtobufIcons.RPC_METHOD_BISTREAM
            outputStream -> ProtobufIcons.RPC_METHOD_SERVER_STREAM
            inputStream -> ProtobufIcons.RPC_METHOD_CLIENT_STREAM
            else -> ProtobufIcons.RPC_METHOD
        }
    }

    fun input(): ProtobufRpcIO? {
        return findChild()
    }

    fun output(): ProtobufRpcIO? {
        return findLastChild()
    }

    override fun tailText(): String? {
        val parameters = findChildren<ProtobufRpcIO>()
        if (parameters.size != 2) return "()"
        var input = parameters[0].typeName.symbolNameList.lastOrNull()?.text ?: return "()"
        var output = parameters[1].typeName.symbolNameList.lastOrNull()?.text ?: return "()"
        if (parameters[0].stream()) {
            input = "stream $input"
        }
        if (parameters[1].stream()) {
            output = "stream $output"
        }
        return "($input): $output"
    }
}
