package io.kanro.idea.plugin.protobuf.string.case

interface CaseFormatter {
    fun format(words: Iterable<String>): String
}
