package io.kanro.idea.plugin.protobuf.lang.psi.feature

import com.intellij.psi.PsiElement

interface QualifiedElement<T : QualifiedElement<T>> : ReferenceElement {
    fun root(): T

    fun leaf(): T

    override fun resolve(): PsiElement? {
        return leaf().reference?.resolve()
    }
}
