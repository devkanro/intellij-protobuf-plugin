package io.kanro.idea.plugin.protobuf.lang.util

import com.google.common.xml.XmlEscapers

fun doc(block: XmlDocument.() -> Unit): String {
    return XmlDocument().apply(block).toString()
}

interface XmlElement {
    fun buildString(builder: StringBuilder)
}

interface CompositeXmlElement : XmlElement {
    fun children(): List<XmlElement>

    fun addChild(child: XmlElement)

    fun text(content: String?) {
        content ?: return
        addChild(TextChunk(content))
    }

    fun bold(content: String?) {
        content ?: return
        tag("b") {
            text(content)
        }
    }

    fun bold(block: XmlChunk.() -> Unit) {
        tag("b", block)
    }

    fun italic(content: String?) {
        content ?: return
        tag("i") {
            text(content)
        }
    }

    fun italic(block: XmlChunk.() -> Unit) {
        tag("i", block)
    }

    fun code(content: String?) {
        content ?: return
        tag("code") {
            text(content)
        }
    }

    fun code(block: XmlChunk.() -> Unit) {
        tag("code", block)
    }

    fun p(content: String?) {
        content ?: return
        tag("p") {
            text(content)
        }
    }

    fun p(block: XmlChunk.() -> Unit) {
        tag("p", block)
    }

    fun br() {
        addChild(XmlChunk("br"))
    }

    fun hr() {
        addChild(XmlChunk("hr"))
    }

    fun strikethrough(content: String?) {
        content ?: return
        tag("s") {
            text(content)
        }
    }

    fun strikethrough(block: XmlChunk.() -> Unit) {
        tag("s", block)
    }

    fun grayed(content: String?) {
        content ?: return
        tag("span") {
            attr("class", "grayed")
            text(content)
        }
    }

    fun grayed(block: XmlChunk.() -> Unit) {
        tag("span") {
            attr("class", "grayed")
            block()
        }
    }

    fun img(
        url: String,
        block: XmlChunk.() -> Unit = {},
    ) {
        tag("img") {
            attr("src", url)
            block()
        }
    }

    fun img(block: XmlChunk.() -> Unit) {
        tag("img") {
            block()
        }
    }

    fun color(
        color: String,
        block: XmlChunk.() -> Unit,
    ) {
        tag("font") {
            attr("color", color)
            block()
        }
    }

    fun sectionHeader(content: String?) {
        content ?: return
        tag("td") {
            attr("valign", "top")
            attr("class", "section")
            text(content)
        }
    }

    fun sectionHeader(block: XmlChunk.() -> Unit) {
        tag("td") {
            attr("valign", "top")
            attr("class", "section")
            block()
        }
    }

    fun section(content: String) {
        tag("td") {
            attr("valign", "top")
            text(content)
        }
    }

    fun section(block: XmlChunk.() -> Unit) {
        tag("td") {
            attr("valign", "top")
            block()
        }
    }

    fun link(
        content: String,
        url: String = "",
    ) {
        tag("a") {
            attr("href", url)
            text(content)
        }
    }

    fun link(
        url: String = "",
        block: XmlChunk.() -> Unit,
    ) {
        tag("a") {
            attr("href", url)
            block()
        }
    }

    fun tag(
        tag: String,
        block: XmlChunk.() -> Unit,
    ) {
        addChild(XmlChunk(tag).apply { this.block() })
    }
}

class TextChunk(
    val content: String,
) : XmlElement {
    override fun buildString(builder: StringBuilder) {
        builder.append(content.xmlContentEscape())
    }
}

class XmlChunk(
    val tag: String,
    val attributes: MutableMap<String, String> = mutableMapOf(),
    val children: MutableList<XmlElement> = mutableListOf(),
) : CompositeXmlElement {
    fun attr(
        name: String,
        value: String,
    ) {
        attributes[name] = value
    }

    override fun addChild(child: XmlElement) {
        children += child
    }

    override fun children(): List<XmlElement> {
        return children
    }

    override fun buildString(builder: StringBuilder) {
        builder.append("<$tag")
        attributes.forEach { (t, u) ->
            builder.append(" $t=\"${u.xmlAttributeEscape()}\"")
        }
        if (children.isEmpty()) {
            builder.append("/>")
        } else {
            builder.append(">")
            children.forEach {
                it.buildString(builder)
            }
            builder.append("</$tag>")
        }
    }
}

class XmlDocument(val children: MutableList<XmlElement> = mutableListOf()) : CompositeXmlElement {
    fun definition(block: XmlChunk.() -> Unit) {
        addChild(
            XmlChunk("div").apply {
                attr("class", "definition")
                block()
            },
        )
    }

    fun content(block: XmlChunk.() -> Unit) {
        addChild(
            XmlChunk("div").apply {
                attr("class", "content")
                block()
            },
        )
    }

    fun sections(block: XmlChunk.() -> Unit) {
        addChild(
            XmlChunk("table").apply {
                attr("class", "sections")
                block()
            },
        )
    }

    override fun addChild(child: XmlElement) {
        children += child
    }

    override fun children(): List<XmlElement> {
        return children
    }

    override fun buildString(builder: StringBuilder) {
        children.forEach {
            it.buildString(builder)
        }
    }

    override fun toString(): String {
        return buildString {
            buildString(this)
        }
    }
}

@Suppress("UnstableApiUsage")
fun String.xmlAttributeEscape(): String {
    return XmlEscapers.xmlAttributeEscaper().escape(this)
}

@Suppress("UnstableApiUsage")
fun String.xmlContentEscape(): String {
    return XmlEscapers.xmlContentEscaper().escape(this)
}
