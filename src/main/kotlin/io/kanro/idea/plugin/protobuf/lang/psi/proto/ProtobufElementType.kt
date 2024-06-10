package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.ILazyParseableElementType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage

open class ProtobufElementType(name: String) : IElementType(name, ProtobufLanguage)

open class ProtoTextInjectionElementType(name: String) : ILazyParseableElementType(name, ProtobufLanguage) {
    override fun parseContents(chameleon: ASTNode): ASTNode {
        return super.parseContents(chameleon)
    }
}
