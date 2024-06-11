package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.intellij.openapi.util.TextRange

fun ProtobufStringValue.stringRange(): TextRange {
    return stringRange(textRange)
}

fun ProtobufStringValue.stringRangeInParent(): TextRange {
    return stringRange(textRangeInParent)
}

private fun ProtobufStringValue.stringRange(relativelyRange: TextRange): TextRange {
    var textRange = relativelyRange
    val text = text

    if (textRange.length == 0) return textRange
    if (text.startsWith('"')) {
        textRange = TextRange.create(textRange.startOffset + 1, textRange.endOffset)
    }
    if (textRange.length == 0) return textRange
    if (text.endsWith('"')) {
        textRange = TextRange.create(textRange.startOffset, textRange.endOffset - 1)
    }
    return textRange
}
