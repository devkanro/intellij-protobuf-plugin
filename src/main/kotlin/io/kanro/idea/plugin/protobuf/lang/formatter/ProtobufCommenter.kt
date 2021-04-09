package io.kanro.idea.plugin.protobuf.lang.formatter

import com.intellij.lang.Commenter

class ProtobufCommenter : Commenter {
    override fun getBlockCommentPrefix(): String {
        return "/*"
    }

    override fun getBlockCommentSuffix(): String {
        return "*/"
    }

    override fun getLineCommentPrefix(): String {
        return "//"
    }

    override fun getCommentedBlockCommentPrefix(): String {
        return "**"
    }

    override fun getCommentedBlockCommentSuffix(): String? {
        return null
    }
}
