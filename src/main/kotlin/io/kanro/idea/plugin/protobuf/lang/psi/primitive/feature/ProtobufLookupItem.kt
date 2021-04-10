package io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.navigation.NavigationItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement

interface ProtobufLookupItem : ProtobufElement, NavigationItem {
    fun lookup(): LookupElementBuilder?
}
