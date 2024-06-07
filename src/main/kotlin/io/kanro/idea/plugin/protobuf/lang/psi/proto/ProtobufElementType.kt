package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.psi.tree.IElementType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage

open class ProtobufElementType(name: String) : IElementType(name, ProtobufLanguage)
