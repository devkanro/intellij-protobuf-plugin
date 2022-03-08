package io.kanro.idea.plugin.protobuf.buf.util

import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

private val extensions = listOf(AutolinkExtension.create(), TablesExtension.create())
private val renderer = HtmlRenderer.builder().extensions(extensions).build()

fun renderDoc(doc: String): String {
    val parser =
        Parser.builder().extensions(extensions).build()
    return renderer.render(parser.parse(doc))
}
