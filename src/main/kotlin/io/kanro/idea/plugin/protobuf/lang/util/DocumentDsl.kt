package io.kanro.idea.plugin.protobuf.lang.util

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.util.text.HtmlBuilder
import com.intellij.openapi.util.text.HtmlChunk

fun doc(block: DocumentScope.() -> Unit): String {
    return DocumentScope().apply(block).toString()
}

@DslMarker
annotation class DocumentDsl

@DocumentDsl
abstract class AbstractDocumentScope<T : AbstractDocumentScope<T>> {
    fun text(content: String?) {
        addElement(HtmlChunk.text(content ?: return))
    }

    fun bold(content: String?) {
        tag("b") {
            text(content)
        }
    }

    fun bold(block: ElementScope.() -> Unit) {
        tag("b", block)
    }

    fun italic(content: String?) {
        tag("i") {
            text(content)
        }
    }

    fun italic(block: ElementScope.() -> Unit) {
        tag("i", block)
    }

    fun code(content: String?) {
        tag("code") {
            text(content)
        }
    }

    fun code(block: ElementScope.() -> Unit) {
        tag("code", block)
    }

    fun p(content: String?) {
        tag("p") {
            text(content)
        }
    }

    fun p(block: ElementScope.() -> Unit) {
        tag("p", block)
    }

    fun br() {
        addElement(HtmlChunk.br())
    }

    fun hr() {
        addElement(HtmlChunk.hr())
    }

    fun strikethrough(content: String?) {
        tag("s") {
            text(content)
        }
    }

    fun strikethrough(block: ElementScope.() -> Unit) {
        tag("s", block)
    }

    fun grayed(content: String?) {
        tag("span") {
            attr("class", "grayed")
            text(content)
        }
    }

    fun grayed(block: ElementScope.() -> Unit) {
        tag("span") {
            attr("class", "grayed")
            block()
        }
    }

    fun img(url: String, block: ElementScope.() -> Unit = {}) {
        tag("img") {
            attr("src", url)
            block()
        }
    }

    fun img(block: ElementScope.() -> Unit) {
        tag("img") {
            block()
        }
    }

    fun color(color: String, block: ElementScope.() -> Unit) {
        tag("font") {
            attr("color", color)
            block()
        }
    }

    fun sectionHeader(content: String?) {
        addElement(
            ElementScope(DocumentationMarkup.SECTION_HEADER_CELL).apply {
                text(content)
            }.element
        )
    }

    fun sectionHeader(block: ElementScope.() -> Unit) {
        addElement(ElementScope(DocumentationMarkup.SECTION_HEADER_CELL).apply(block).element)
    }

    fun section(content: String) {
        addElement(
            ElementScope(DocumentationMarkup.SECTION_CONTENT_CELL).apply {
                text(content)
            }.element
        )
    }

    fun section(block: ElementScope.() -> Unit) {
        addElement(ElementScope(DocumentationMarkup.SECTION_CONTENT_CELL).apply(block).element)
    }

    fun link(content: String, url: String = "") {
        tag("a") {
            attr("href", url)
            text(content)
        }
    }

    fun link(url: String = "", block: ElementScope.() -> Unit) {
        tag("a") {
            attr("href", url)
            block()
        }
    }

    private inline fun tag(tag: String, block: ElementScope.() -> Unit) {
        addElement(ElementScope(HtmlChunk.tag(tag)).apply(block).element)
    }

    protected abstract fun addElement(chunk: HtmlChunk)
}

@DocumentDsl
class DocumentScope(val builder: HtmlBuilder = HtmlBuilder()) : AbstractDocumentScope<DocumentScope>() {
    fun definition(block: ElementScope.() -> Unit) {
        addElement(ElementScope(DocumentationMarkup.DEFINITION_ELEMENT).apply(block).element)
    }

    fun content(block: ElementScope.() -> Unit) {
        addElement(ElementScope(DocumentationMarkup.CONTENT_ELEMENT).apply(block).element)
    }

    fun sections(block: ElementScope.() -> Unit) {
        addElement(ElementScope(DocumentationMarkup.SECTIONS_TABLE).apply(block).element)
    }

    override fun addElement(chunk: HtmlChunk) {
        builder.append(chunk)
    }

    override fun toString(): String {
        return builder.toString()
    }
}

@DocumentDsl
class ElementScope(var element: HtmlChunk.Element) : AbstractDocumentScope<ElementScope>() {
    fun attr(name: String, value: String) {
        element = element.attr(name, value)
    }

    override fun addElement(chunk: HtmlChunk) {
        element = element.child(chunk)
    }
}
