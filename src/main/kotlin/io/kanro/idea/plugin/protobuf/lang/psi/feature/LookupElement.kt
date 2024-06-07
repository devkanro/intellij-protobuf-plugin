package io.kanro.idea.plugin.protobuf.lang.psi.feature

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.navigation.NavigationItem
import io.kanro.idea.plugin.protobuf.lang.psi.BaseElement

interface LookupElement : BaseElement, NavigationItem {
    fun lookup(name: String? = null): LookupElementBuilder?
}
