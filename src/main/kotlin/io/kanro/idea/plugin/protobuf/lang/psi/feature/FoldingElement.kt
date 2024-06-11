package io.kanro.idea.plugin.protobuf.lang.psi.feature

import com.intellij.lang.folding.FoldingDescriptor
import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement

interface FoldingElement : BaseElement {
    fun folding(): FoldingDescriptor?
}
