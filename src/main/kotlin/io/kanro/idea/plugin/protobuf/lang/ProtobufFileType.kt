package io.kanro.idea.plugin.protobuf.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import io.kanro.idea.plugin.protobuf.Icons
import javax.swing.Icon

object ProtobufFileType : LanguageFileType(ProtobufLanguage) {
    override fun getName(): String {
        return ProtobufLanguage.id
    }

    override fun getDescription(): String {
        return "Protocol Buffer"
    }

    override fun getDefaultExtension(): String {
        return "proto"
    }

    override fun getIcon(): Icon {
        return Icons.FILE
    }
}
