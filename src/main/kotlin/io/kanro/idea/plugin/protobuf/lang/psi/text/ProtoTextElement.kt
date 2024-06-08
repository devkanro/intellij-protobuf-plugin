package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile

interface ProtoTextElement : BaseElement {
    fun file(): ProtobufFile? {
        return when (val file = containingFile.originalFile) {
            is ProtobufFile -> file
            is ProtoTextFile -> file.descriptorFile()
            else -> null
        }
    }

    fun scopeElement(): ProtobufElement? {
        return when (val file = containingFile.originalFile) {
            is ProtobufFile -> parentOfType<ProtobufElement>()
            is ProtoTextFile -> file.descriptorFile()
            else -> null
        }
    }
}
