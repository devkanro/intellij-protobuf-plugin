package io.kanro.idea.plugin.protobuf.lang.psi.token

import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.ProtoBaseLanguage

open class ProtobufToken(name: String) : IElementType(name, ProtoBaseLanguage)
