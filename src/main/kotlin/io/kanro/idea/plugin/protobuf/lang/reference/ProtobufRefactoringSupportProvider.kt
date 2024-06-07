package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement

class ProtobufRefactoringSupportProvider : RefactoringSupportProvider() {
    override fun isMemberInplaceRenameAvailable(
        elementToRename: PsiElement,
        context: PsiElement?,
    ): Boolean {
        return elementToRename is NamedElement
    }
}
