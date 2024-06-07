package io.kanro.idea.plugin.protobuf.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type.ProtobufStubTypes
import javax.swing.Icon

class ProtobufFileType : LanguageFileType(ProtobufLanguage) {
    companion object {
        val INSTANCE = ProtobufFileType()

        init {
            // Workaround for issue #164
            ProtobufStubTypes
        }
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
        return ProtobufIcons.FILE
    }
}
