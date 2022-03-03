package io.kanro.idea.plugin.protobuf

import com.intellij.openapi.util.IconLoader
import com.intellij.util.PlatformIcons
import javax.swing.Icon

object Icons {
    var ENUM: Icon = loadIcon("enum.svg")
    var ENUM_VALUE: Icon = loadIcon("enum_value.svg")
    var FIELD: Icon = loadIcon("field.svg")
    var EXTEND: Icon = loadIcon("extend.svg")
    var GROUP_FIELD: Icon = loadIcon("group_field.svg")
    var ONEOF: Icon = loadIcon("oneof_field.svg")
    var PACKAGE: Icon = PlatformIcons.PACKAGE_ICON
    var SERVICE: Icon = loadIcon("service.svg")
    var RPC_METHOD: Icon = loadIcon("rpc_method.svg")
    var RPC_METHOD_CLIENT_STREAM: Icon = loadIcon("rpc_method_client_stream.svg")
    var RPC_METHOD_SERVER_STREAM: Icon = loadIcon("rpc_method_server_stream.svg")
    var RPC_METHOD_BISTREAM: Icon = loadIcon("rpc_method_bistream.svg")

    var MESSAGE: Icon = loadIcon("message.svg")
    var RESOURCE_MESSAGE: Icon = loadIcon("resource_message.svg")
    var FILE: Icon = loadIcon("protobuf_file.svg")
    var LOGO: Icon = loadIcon("logo.svg")
    var FOLDER: Icon = PlatformIcons.FOLDER_ICON

    var IMPLEMENTED_SERVICE: Icon = loadIcon("implementedService.svg")
    var IMPLEMENTING_SERVICE: Icon = loadIcon("implementingService.svg")

    var IMPLEMENTED_RPC: Icon = loadIcon("implementedRpc.svg")
    var IMPLEMENTING_RPC: Icon = loadIcon("implementingRpc.svg")

    var PROCEDURE: Icon = loadIcon("procedure.svg")
    var PROTO_DECOMPILE: Icon = loadIcon("proto_decompile.svg")

    var ARRANGE_FROM_MIN: Icon = loadIcon("arrangeFromMin.svg")
    var ARRANGE_TO_MAX: Icon = loadIcon("arrangeToMax.svg")

    private fun loadIcon(name: String): Icon {
        return IconLoader.getIcon("/io/kanro/idea/plugin/protobuf/icon/$name", Icons::class.java)
    }
}
