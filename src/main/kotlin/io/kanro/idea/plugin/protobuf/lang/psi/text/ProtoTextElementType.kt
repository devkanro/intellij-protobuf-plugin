package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.ProtoTextLanguage

open class ProtoTextElementType(name: String) : IElementType(name, ProtoTextLanguage)
