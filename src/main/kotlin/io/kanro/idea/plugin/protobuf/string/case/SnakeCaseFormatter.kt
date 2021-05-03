package io.kanro.idea.plugin.protobuf.string.case

object SnakeCaseFormatter : BaseCaseFormatter() {
    override fun formatWord(index: Int, word: CharSequence): CharSequence {
        return word.toString().toLowerCase()
    }

    override fun appendDelimiter(builder: StringBuilder) {
        builder.append('_')
    }
}