package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.intellij.lang.DefaultASTFactoryImpl
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.psi.text.impl.ProtoTextSharpLineCommentImpl
import io.kanro.idea.plugin.protobuf.lang.psi.text.token.ProtoTextTokens

class ProtoTextASTFactory : DefaultASTFactoryImpl() {
    override fun createComment(
        type: IElementType,
        text: CharSequence,
    ): LeafElement {
        return if (type == ProtoTextTokens.SHARP_LINE_COMMENT) {
            ProtoTextSharpLineCommentImpl(type, text)
        } else {
            PsiCommentImpl(type, text)
        }
    }
}
