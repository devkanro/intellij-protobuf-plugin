package io.kanro.idea.plugin.protobuf.lang.psi.text.token

import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.ProtoTextLanguage

open class ProtoTextToken(name: String) : IElementType(name, ProtoTextLanguage)
