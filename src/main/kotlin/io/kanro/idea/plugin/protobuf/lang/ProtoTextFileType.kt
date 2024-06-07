package io.kanro.idea.plugin.protobuf.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.type.ProtobufStubTypes
import javax.swing.Icon

class ProtoTextFileType : LanguageFileType(ProtoTextLanguage) {
    companion object {
        val INSTANCE = ProtoTextFileType()

        init {
            // Workaround for issue #164
            ProtobufStubTypes
        }
    }

    override fun getName(): String {
        return ProtoTextLanguage.id
    }

    override fun getDescription(): String {
        return "Protocol Buffer Text Format"
    }

    override fun getDefaultExtension(): String {
        return "txtpb"
    }

    override fun getIcon(): Icon {
        return ProtobufIcons.TEXT_FILE
    }
}
