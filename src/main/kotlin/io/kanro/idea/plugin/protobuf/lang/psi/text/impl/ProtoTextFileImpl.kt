package io.kanro.idea.plugin.protobuf.lang.psi.text.impl

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.ProtoTextFileType
import io.kanro.idea.plugin.protobuf.lang.ProtoTextLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFile
import javax.swing.Icon

class ProtoTextFileImpl(viewProvider: FileViewProvider) :
    PsiFileBase(viewProvider, ProtoTextLanguage),
    ProtoTextFile {
    override fun getFileType(): FileType {
        return ProtoTextFileType.INSTANCE
    }

    override fun toString(): String {
        return "Protobuf File Text Format"
    }

    override fun getPresentableText(): String? {
        return name()
    }

    override fun name(): String {
        return this.name
    }

    override fun getPresentation(): ItemPresentation? {
        return this
    }

    override fun getLocationString(): String? {
        return null
    }

    override fun getIcon(unused: Boolean): Icon? {
        return ProtobufIcons.TEXT_FILE
    }
}
