package io.kanro.idea.plugin.protobuf.buf.schema

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange

class BufSchemaValidationException(
    val severity: HighlightSeverity,
    message: String,
    val range: TextRange,
    val highlightType: ProblemHighlightType? = null,
    val fix: IntentionAction? = null
) : RuntimeException(message)
