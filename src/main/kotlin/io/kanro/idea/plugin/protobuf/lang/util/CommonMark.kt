package io.kanro.idea.plugin.protobuf.lang.util

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

private val parser: Parser = Parser.builder().build()
private val renderer = HtmlRenderer.builder().build()

fun renderDoc(doc: String): String {
    return renderer.render(parser.parse(doc))
}
