package io.kanro.idea.plugin.protobuf.decompile

class ProtobufCodeBuilder {
    private val builder = StringBuilder()
    private var indent = 0

    fun indent(): ProtobufCodeBuilder {
        indent++
        return this
    }

    fun deindent(): ProtobufCodeBuilder {
        if (indent == 0) throw IllegalStateException("Wrong indent")
        indent--
        return this
    }

    fun ln(): ProtobufCodeBuilder {
        builder.appendLine()
        return this
    }

    fun normalizeLn(): ProtobufCodeBuilder {
        if (builder.isEmpty()) return this

        while (builder.last() == '\n') {
            builder.deleteAt(builder.lastIndex)
        }
        return ln()
    }

    fun normalizeStatementLn(): ProtobufCodeBuilder {
        if (builder.isEmpty()) return this
        normalizeLn()
        if (builder[builder.lastIndex - 1] != '{') {
            ln()
        }
        return this
    }

    fun append(text: String): ProtobufCodeBuilder {
        if (builder.isEmpty() || builder.last() == '\n') {
            builder.append(indentCode(text, " ".repeat(indent * 4)))
        } else {
            builder.append(text)
        }
        return this
    }

    fun appendLn(text: String): ProtobufCodeBuilder {
        return append(text).ln()
    }

    fun beginBlock(code: String): ProtobufCodeBuilder {
        return appendLn("$code {").indent()
    }

    fun endBlock(): ProtobufCodeBuilder {
        return deindent().normalizeLn().appendLn("}")
    }

    fun block(code: String, block: ProtobufCodeBuilder.() -> Unit) {
        beginBlock(code).apply(block).endBlock()
    }

    override fun toString(): String {
        return builder.toString()
    }

    companion object {
        private val indentRegex = """^(?!\s*$)""".toRegex(RegexOption.MULTILINE)

        private fun indentCode(content: String, indentation: String): String {
            if (indentation.isEmpty()) return content
            return indentRegex.replace(content, indentation)
        }
    }
}

fun buildProtobuf(block: ProtobufCodeBuilder.() -> Unit): String {
    return ProtobufCodeBuilder().apply(block).toString()
}
