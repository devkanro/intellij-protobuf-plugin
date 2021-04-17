package io.kanro.idea.plugin.protobuf.string.case

interface WordSplitter {
    fun split(string: CharSequence): List<String>
}
