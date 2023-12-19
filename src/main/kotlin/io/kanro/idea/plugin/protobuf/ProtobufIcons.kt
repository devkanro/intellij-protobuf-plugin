package io.kanro.idea.plugin.protobuf

import com.intellij.openapi.util.IconLoader
import com.intellij.util.PlatformIcons
import javax.swing.Icon

object ProtobufIcons {
    val ENUM: Icon = loadIcon("enum.svg")
    val ENUM_VALUE: Icon = loadIcon("enum_value.svg")
    val FIELD: Icon = loadIcon("field.svg")
    val EXTEND: Icon = loadIcon("extend.svg")
    val GROUP_FIELD: Icon = loadIcon("group_field.svg")
    val ONEOF: Icon = loadIcon("oneof_field.svg")
    val PACKAGE: Icon = PlatformIcons.PACKAGE_ICON
    val SERVICE: Icon = loadIcon("service.svg")
    val RPC_METHOD: Icon = loadIcon("rpc_method.svg")
    val RPC_METHOD_CLIENT_STREAM: Icon = loadIcon("rpc_method_client_stream.svg")
    val RPC_METHOD_SERVER_STREAM: Icon = loadIcon("rpc_method_server_stream.svg")
    val RPC_METHOD_BISTREAM: Icon = loadIcon("rpc_method_bistream.svg")

    val MESSAGE: Icon = loadIcon("message.svg")
    val RESOURCE_MESSAGE: Icon = loadIcon("resource_message.svg")
    val FILE: Icon = loadIcon("protobuf_file.svg")
    val LOGO: Icon = loadIcon("logo.svg")
    val FOLDER: Icon = PlatformIcons.FOLDER_ICON

    val IMPLEMENTED_SERVICE: Icon = loadIcon("implementedService.svg")
    val IMPLEMENTING_SERVICE: Icon = loadIcon("implementingService.svg")

    val IMPLEMENTED_RPC: Icon = loadIcon("implementedRpc.svg")
    val IMPLEMENTING_RPC: Icon = loadIcon("implementingRpc.svg")

    val PROCEDURE: Icon = loadIcon("procedure.svg")
    val PROCEDURE_HTTP: Icon = loadIcon("procedureHttp.svg")
    val PROTO_DECOMPILE: Icon = loadIcon("proto_decompile.svg")

    val ARRANGE_FROM_MIN: Icon = loadIcon("arrangeFromMin.svg")
    val ARRANGE_TO_MAX: Icon = loadIcon("arrangeToMax.svg")

    val BUF_LOGO: Icon = loadIcon("buf.svg")

    val BUF_LOGO_13: Icon = loadIcon("buf13.svg")

    private fun loadIcon(name: String): Icon {
        return IconLoader.getIcon("/io/kanro/idea/plugin/protobuf/icon/$name", ProtobufIcons::class.java)
    }
}
