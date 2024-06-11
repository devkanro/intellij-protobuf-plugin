package io.kanro.idea.plugin.protobuf.lang.psi.feature

import com.intellij.psi.PsiElement
import com.intellij.psi.util.QualifiedName

interface ReferenceElement : PsiElement {
    fun symbol(): QualifiedName?

    fun rename(qualifiedName: QualifiedName)

    fun resolve(): PsiElement? {
        return reference?.resolve()
    }
}
