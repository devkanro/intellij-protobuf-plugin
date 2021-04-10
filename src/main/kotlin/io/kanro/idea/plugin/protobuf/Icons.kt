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

    var MESSAGE: Icon = loadIcon("message.svg")
    var FILE: Icon = loadIcon("protobuf_file.svg")
    var LOGO: Icon = loadIcon("logo.svg")

    private fun loadIcon(name: String): Icon {
        return IconLoader.getIcon("/io/kanro/idea/plugin/protobuf/icon/$name", Icons::class.java)
    }
}
