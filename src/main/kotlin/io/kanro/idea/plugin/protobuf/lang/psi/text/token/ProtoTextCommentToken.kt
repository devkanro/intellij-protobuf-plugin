package io.kanro.idea.plugin.protobuf.lang.psi.text.token

import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.ProtoTextLanguage

open class ProtoTextCommentToken(name: String) : IElementType(name, ProtoTextLanguage)
