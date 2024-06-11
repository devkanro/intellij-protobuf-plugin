package io.kanro.idea.plugin.protobuf.lang.psi.proto.token

import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage

open class ProtobufCommentToken(name: String) : IElementType(name, ProtobufLanguage)
