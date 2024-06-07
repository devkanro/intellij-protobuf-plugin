package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.lang.DefaultASTFactoryImpl
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.impl.ProtobufLineCommentImpl
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens

class ProtobufASTFactory : DefaultASTFactoryImpl() {
    override fun createComment(
        type: IElementType,
        text: CharSequence,
    ): LeafElement {
        return if (type == ProtobufTokens.LINE_COMMENT) {
            ProtobufLineCommentImpl(type, text)
        } else {
            PsiCommentImpl(type, text)
        }
    }
}
