package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.lang.folding.FoldingDescriptor
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufFolding : ProtobufElement {
    fun folding(): FoldingDescriptor?
}
