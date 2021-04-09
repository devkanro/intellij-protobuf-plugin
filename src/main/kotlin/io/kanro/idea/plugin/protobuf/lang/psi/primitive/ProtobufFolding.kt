package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import com.intellij.lang.folding.FoldingDescriptor

interface ProtobufFolding : ProtobufElement {
    fun folding(): FoldingDescriptor?
}
