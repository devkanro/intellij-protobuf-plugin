package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.stubs.IStubElementType
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufNamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.stub.impl.ProtobufStubBase

abstract class StubBasedProtobufElementBase<T : ProtobufStubBase<*>> : StubBasedPsiElementBase<T> {
    constructor(node: ASTNode) : super(node)

    constructor(stub: T, type: IStubElementType<*, *>) : super(stub, type)

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
        if (this is ProtobufNamedElement) return name()
        return null
    }
}
