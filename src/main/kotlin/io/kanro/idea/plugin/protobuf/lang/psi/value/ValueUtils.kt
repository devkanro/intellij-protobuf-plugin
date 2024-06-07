package io.kanro.idea.plugin.protobuf.lang.psi.value

import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens

fun decodeStringFromStringLiteral(textElement: PsiElement): String {
    if (textElement.elementType != ProtobufTokens.STRING_LITERAL) {
        throw IllegalArgumentException("Element is not a string literal")
    }

    return decodeString(textElement.text.trim('"', '\''))
}

fun decodeString(text: String): String =
    buildString {
        var index = 0
        while (index < text.length) {
            val char = text[index]
            if (char == '\\') {
                index++
                if (index >= text.length) {
                    break
                }
                val next = text[index]
                when (next) {
                    'a' -> append('\u0007')
                    'b' -> append('\b')
                    'f' -> append('\u000C')
                    'n' -> append('\n')
                    'r' -> append('\r')
                    't' -> append('\t')
                    'v' -> append('\u000B')
                    'x' -> {
                        index++
                        if (index >= text.length) {
                            break
                        }
                        val hex = text.substring(index, index + 2)
                        index += 2
                        append(hex.toInt(16).toChar())
                    }

                    'u' -> {
                        index++
                        if (index >= text.length) {
                            break
                        }
                        val hex = text.substring(index, index + 4)
                        index += 4
                        append(hex.toInt(16).toChar())
                    }

                    else -> append(next)
                }
            } else {
                append(char)
            }
            index++
        }
    }
