package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import com.intellij.codeInsight.lookup.LookupElementBuilder

interface ProtobufLookupItem : ProtobufElement {
    fun lookup(): LookupElementBuilder?
}
