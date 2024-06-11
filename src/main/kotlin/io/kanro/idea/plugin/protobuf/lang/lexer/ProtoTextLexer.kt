package io.kanro.idea.plugin.protobuf.lang.lexer

import com.intellij.lexer.FlexAdapter
import io.kanro.idea.plugin.protobuf.lang.lexer.text._ProtoTextLexer

class ProtoTextLexer : FlexAdapter(_ProtoTextLexer(null))
