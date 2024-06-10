package io.kanro.idea.plugin.protobuf.lang.util

import com.intellij.codeInsight.documentation.DocumentationManagerProtocol
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.internal.InlineParserImpl
import org.commonmark.node.LinkReferenceDefinition
import org.commonmark.parser.InlineParser
import org.commonmark.parser.InlineParserContext
import org.commonmark.parser.InlineParserFactory
import org.commonmark.parser.Parser
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.renderer.html.HtmlRenderer

private val extensions = listOf(AutolinkExtension.create(), TablesExtension.create())
private val renderer = HtmlRenderer.builder().extensions(extensions).build()

class ProtoDocInlineParserFactory(private val context: ProtobufElement) : InlineParserFactory {
    override fun create(inlineParserContext: InlineParserContext): InlineParser {
        return InlineParserImpl(ProtoDocInlineParserContext(context, inlineParserContext))
    }
}

class ProtoDocInlineParserContext(private val context: ProtobufElement, private val delegate: InlineParserContext) :
    InlineParserContext {
    override fun getCustomDelimiterProcessors(): MutableList<DelimiterProcessor> {
        return delegate.customDelimiterProcessors
    }

    override fun getLinkReferenceDefinition(label: String): LinkReferenceDefinition {
        delegate.getLinkReferenceDefinition(label)?.let {
            return it
        }
        val path =
            DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL +
                label +
                DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL_REF_SEPARATOR +
                label
        return LinkReferenceDefinition(label, path, label)
    }
}

fun renderDoc(
    context: ProtobufElement,
    doc: String,
): String {
    val parser =
        Parser.builder().extensions(extensions).inlineParserFactory(ProtoDocInlineParserFactory(context)).build()
    return renderer.render(parser.parse(doc))
}
