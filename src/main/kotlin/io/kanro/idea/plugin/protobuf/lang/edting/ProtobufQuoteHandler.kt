package io.kanro.idea.plugin.protobuf.lang.edting

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import io.kanro.idea.plugin.protobuf.lang.psi.proto.token.ProtobufTokens

class ProtobufQuoteHandler : SimpleTokenSetQuoteHandler(ProtobufTokens.STRING_LITERAL)
