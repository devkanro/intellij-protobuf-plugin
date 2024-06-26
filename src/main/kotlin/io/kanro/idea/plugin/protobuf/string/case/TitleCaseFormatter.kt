package io.kanro.idea.plugin.protobuf.string.case

object TitleCaseFormatter : BaseCaseFormatter() {
    private val lowerCaseWord: Set<String> =
        hashSetOf(
            "a", "an", "the",
            "and", "but", "or",
            "on", "in", "with", "at", "by",
            "of", "from", "for", "to",
        )

    override fun formatWord(
        index: Int,
        word: CharSequence,
    ): CharSequence {
        val lowerCase = word.toString().lowercase()

        return if (lowerCaseWord.contains(lowerCase)) {
            lowerCase
        } else {
            buildString {
                append(lowerCase.first().uppercase())
                append(lowerCase.substring(1))
            }
        }
    }

    override fun appendDelimiter(builder: StringBuilder) {
        builder.append(' ')
    }
}
