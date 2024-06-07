package io.kanro.idea.plugin.protobuf.lang.psi.text

import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement

interface ProtoTextElement : BaseElement {
    fun file(): ProtoTextFile {
        return containingFile.originalFile as ProtoTextFile
    }
}
