package io.kanro.idea.plugin.protobuf.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import io.kanro.idea.plugin.protobuf.Icons
import javax.swing.Icon

class ProtobufFileType : LanguageFileType(ProtobufLanguage) {
    companion object {
        val INSTANCE = ProtobufFileType()
    }

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
