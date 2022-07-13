package io.kanro.idea.plugin.protobuf

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.IconPathPatcher
import com.intellij.ui.ExperimentalUI

class ProtobufExpUiHelper : LafManagerListener {
    override fun lookAndFeelChanged(source: LafManager) {
        if (ExperimentalUI.isNewUI()) {
            IconLoader.installPathPatcher(ProtobufExpUiIconsPatcher)
        } else {
            IconLoader.removePathPatcher(ProtobufExpUiIconsPatcher)
        }
    }

    object ProtobufExpUiIconsPatcher : IconPathPatcher() {
        override fun patchPath(path: String, classLoader: ClassLoader?): String? {
            classLoader ?: return null

            if (path.startsWith("/io/kanro/idea/plugin/protobuf/icon/")) {
                val new =
                    path.replace("/io/kanro/idea/plugin/protobuf/icon/", "/io/kanro/idea/plugin/protobuf/icon/expui/")
                if (classLoader.getResource(new.substring(1)) == null) return null
                return new
            } else {
                return null
            }
        }
    }
}