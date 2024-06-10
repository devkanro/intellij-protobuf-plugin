package io.kanro.idea.plugin.protobuf.lang.lexer

import com.intellij.lexer.FlexAdapter
import io.kanro.idea.plugin.protobuf.lang.lexer.proto._ProtobufLexer

class ProtobufLexer : FlexAdapter(_ProtobufLexer(null))
