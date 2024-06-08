package io.kanro.idea.plugin.protobuf.lang.psi.proto.impl

import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.refactoring.suggested.endOffset
import io.kanro.idea.plugin.protobuf.lang.psi.feature.DocumentElement
import io.kanro.idea.plugin.protobuf.lang.psi.feature.FoldingElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.util.renderDoc

class ProtobufLineCommentImpl(type: IElementType, text: CharSequence) :
    PsiCommentImpl(type, text), DocumentElement, FoldingElement, ProtobufElement {
    override fun getOwner(): PsiElement? {
        this.prevSibling?.let {
            if (it !is PsiWhiteSpace) return null
            if (!it.text.contains('\n')) return null
        }
        return super.getOwner()
    }

    override fun render(): String {
        return CachedValuesManager.getCachedValue(this) {
            val result = renderDoc(this, text.trimMargin("//"))
            CachedValueProvider.Result.create(result, PsiModificationTracker.MODIFICATION_COUNT)
        }
    }

    override fun folding(): FoldingDescriptor? {
        return FoldingDescriptor(
            this,
            startOffset,
            endOffset,
            null,
            "//...",
        )
    }

    override fun toString(): String {
        return "ProtobufLineComment($elementType)"
    }
}
