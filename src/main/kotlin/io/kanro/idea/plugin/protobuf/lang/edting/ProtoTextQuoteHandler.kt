package io.kanro.idea.plugin.protobuf.lang.edting

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import io.kanro.idea.plugin.protobuf.lang.psi.text.token.ProtoTextTokens

class ProtoTextQuoteHandler : SimpleTokenSetQuoteHandler(ProtoTextTokens.STRING_LITERAL)
