package io.kanro.idea.plugin.protobuf.lang.psi.text

import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile

interface ProtoTextElement : BaseElement {
    fun schemaFile(): ProtobufFile? {
        return file()?.schemaFile()
    }

    fun file(): ProtoTextFile? {
        return when (val file = containingFile.originalFile) {
            is ProtoTextFile -> file
            else -> null
        }
    }
}
