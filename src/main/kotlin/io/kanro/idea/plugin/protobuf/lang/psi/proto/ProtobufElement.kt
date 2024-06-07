package io.kanro.idea.plugin.protobuf.lang.psi.proto

import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver

interface ProtobufElement : BaseElement {
    fun file(): ProtobufFile {
        return containingFile.originalFile as ProtobufFile
    }

    fun importPath(context: ProtobufFile? = null): String? {
        val file = file()
        return ProtobufRootResolver.getImportPath(file.virtualFile, context ?: file)
    }
}
