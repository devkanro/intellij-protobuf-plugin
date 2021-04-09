package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.QualifiedName

interface ProtobufDefinition : ProtobufElement, PsiNameIdentifierOwner, NavigatablePsiElement {
    fun type(): String

    fun owner(): ProtobufScope?

    fun name(): String?

    fun qualifiedName(): QualifiedName?

    fun identifier(): ProtobufIdentifier?
}

interface ProtobufMultiNameDefinition : ProtobufDefinition {
    fun names(): Set<String>
}
