package io.kanro.idea.plugin.protobuf.string.case

object SpaceCaseFormatter : BaseCaseFormatter() {
    override fun formatWord(
        index: Int,
        word: CharSequence,
    ): CharSequence {
        return word.toString().lowercase()
    }

    override fun appendDelimiter(builder: StringBuilder) {
        builder.append(' ')
    }
}
