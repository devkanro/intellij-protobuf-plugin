package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement

abstract class ProtobufElementBase(node: ASTNode) : ASTWrapperPsiElement(node) {
    override fun getPresentation(): ItemPresentation? {
        if (this is ItemPresentation) return this
        return null
    }

    override fun getTextOffset(): Int {
        if (this is PsiNameIdentifierOwner) {
            if (this.nameIdentifier == this) {
                return super.getTextOffset()
            }
            return this.nameIdentifier?.textOffset ?: super.getTextOffset()
        }
        return super.getTextOffset()
    }

    override fun getName(): String? {
        if (this is NamedElement) return name()
        return null
    }

    override fun getNavigationElement(): PsiElement {
        return if (this is PsiNameIdentifierOwner) {
            this.nameIdentifier ?: this
        } else {
            this
        }
    }
}
