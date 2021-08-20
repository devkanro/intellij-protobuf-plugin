package io.kanro.idea.plugin.protobuf.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScopeItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScopeItemContainer
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufVirtualScope
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufKeywordToken
import io.kanro.idea.plugin.protobuf.string.parseDoubleOrNull
import io.kanro.idea.plugin.protobuf.string.parseLongOrNull
import io.kanro.idea.plugin.protobuf.string.toCamelCase
import java.util.Stack

inline fun <reified T : PsiElement> PsiElement.findChild(): T? {
    var child: PsiElement? = this.firstChild ?: return null
    while (child != null) {
        if (child is T) return child
        child = child.nextSibling
    }
    return null
}

inline fun <reified T : PsiElement> PsiElement.findLastChild(): T? {
    var child: PsiElement? = this.lastChild ?: return null
    while (child != null) {
        if (child is T) return child
        child = child.prevSibling
    }
    return null
}

inline fun <reified T : PsiElement> PsiElement.findChildren(filter: (T) -> Boolean = { true }): Array<T> {
    var child: PsiElement? = this.firstChild ?: return arrayOf()
    val result = mutableListOf<T>()
    while (child != null) {
        if (child is T && filter(child)) result += child
        child = child.nextSibling
    }
    return result.toTypedArray()
}

inline fun <reified T : PsiElement> PsiElement.walkChildren(childTree: Boolean = true, block: (T) -> Unit) {
    val stack = Stack<PsiElement>()
    stack.add(this.firstChild ?: return)

    while (stack.isNotEmpty()) {
        val item = stack.pop()
        if (item is T) block(item)
        if (childTree) {
            item.firstChild?.let {
                stack.add(it)
            }
        }
        item.nextSibling?.let {
            stack.add(it)
        }
    }
}

inline fun PsiElement.walkChildren(filter: PsiElementFilter, childTree: Boolean = true, block: (PsiElement) -> Unit) {
    val stack = Stack<PsiElement>()
    stack.add(this.firstChild ?: return)

    while (stack.isNotEmpty()) {
        val item = stack.pop()
        if (filter.isAccepted(item)) block(item)
        if (childTree) {
            item.firstChild?.let {
                stack.add(it)
            }
        }
        item.nextSibling?.let {
            stack.add(it)
        }
    }
}

inline fun <reified T : ProtobufScopeItem> ProtobufScopeItemContainer.walkItem(block: (T) -> Unit) {
    val stack = Stack<ProtobufElement>()
    stack.add(this)

    while (stack.isNotEmpty()) {
        val item = stack.pop()
        if (item is T) block(item)
        if (item is ProtobufScopeItemContainer) stack.addAll(item.items())
    }
}

inline fun <reified T : PsiElement> PsiElement.next(): T? {
    var next: PsiElement? = this.nextSibling ?: return null
    while (next != null) {
        if (next is T) return next
        next = next.nextSibling
    }
    return null
}

inline fun <reified T : PsiElement> PsiElement.prev(): T? {
    var prev: PsiElement? = this.prevSibling ?: return null
    while (prev != null) {
        if (prev is T) return prev
        prev = prev.prevSibling
    }
    return null
}

fun ProtobufImportStatement.public(): Boolean {
    return importLabel?.textMatches("public") == true
}

fun ProtobufImportStatement.weak(): Boolean {
    return importLabel?.textMatches("weak") == true
}

fun ProtobufImportStatement.resolve(): ProtobufFile? {
    return reference?.resolve() as? ProtobufFile
}

fun ProtobufOptionName.field(): ProtobufFieldLike? {
    this.fieldNameList.lastOrNull()?.let {
        return it.reference?.resolve() as? ProtobufFieldLike
    }
    this.extensionOptionName?.let {
        return it.typeName?.reference?.resolve() as? ProtobufFieldLike
    }
    this.builtInOptionName?.let {
        return it.reference?.resolve() as? ProtobufFieldLike
    }
    return null
}

fun ProtobufFieldName.message(): ProtobufScope? {
    val parent = when (val parent = this.parent) {
        is ProtobufArrayValue -> {
            parent.parent.parent
        }
        else -> parent
    }

    val field = when (parent) {
        is ProtobufOptionName -> {
            val prevField = this.prev<ProtobufFieldName>()
            if (prevField == null) {
                parent.extensionOptionName?.typeName?.reference?.resolve()
            } else {
                prevField.reference?.resolve()
            }
        }
        is ProtobufFieldAssign -> {
            val messageValue = parent.parent as? ProtobufMessageValue ?: return null
            val assign = when (val assign = messageValue.parent.parent) {
                is ProtobufArrayValue -> {
                    assign.parent.parent
                }
                else -> assign
            }

            when (assign) {
                is ProtobufOptionAssign -> {
                    assign.optionName.field()
                }
                is ProtobufFieldAssign -> {
                    assign.fieldName.reference?.resolve() as? ProtobufDefinition
                }
                else -> null
            }
        }
        else -> null
    } ?: return null

    return when (field) {
        is ProtobufGroupDefinition -> field
        is ProtobufFieldDefinition -> field.typeName.reference?.resolve() as? ProtobufMessageDefinition
        else -> null
    }
}

fun ProtobufBuiltInOptionName.isFieldDefaultOption(): Boolean {
    return this.textMatches("default") && parentOfType<ProtobufOptionOwner>() is ProtobufFieldDefinition
}

fun ProtobufBuiltInOptionName.isFieldJsonNameOption(): Boolean {
    return this.textMatches("json_name") && parentOfType<ProtobufOptionOwner>() is ProtobufFieldDefinition
}

fun ProtobufEnumValue.enum(): ProtobufEnumDefinition? {
    val field = when (val parent = this.parent.parent) {
        is ProtobufOptionAssign -> {
            parent.optionName.field() as? ProtobufFieldDefinition
        }
        is ProtobufFieldAssign -> {
            parent.fieldName.reference?.resolve() as? ProtobufFieldDefinition
        }
        else -> null
    } ?: return null
    return field.typeName.reference?.resolve() as? ProtobufEnumDefinition
}

fun ProtobufReservedRange.range(): LongRange? {
    val numbers = integerValueList.map { it.text.toLong() }
    return when (numbers.size) {
        1 -> if (lastChild.textMatches("max")) {
            LongRange(numbers[0], Long.MAX_VALUE)
        } else {
            LongRange(numbers[0], numbers[0])
        }
        2 -> LongRange(numbers[0], numbers[1])
        else -> null
    }
}

operator fun ProtobufScope.iterator(): Iterator<ProtobufScopeItem> {
    return realItems().iterator()
}

inline fun <reified T : ProtobufScopeItem> ProtobufScope.items(block: (T) -> Unit) {
    this.items().forEach {
        when (it) {
            is ProtobufVirtualScope -> {
                it.items().forEach {
                    if (it is T) block(it)
                }
            }
            is T -> {
                block(it)
            }
        }
    }
}

fun ProtobufScope.realItems(): Array<ProtobufScopeItem> {
    val result = mutableListOf<ProtobufScopeItem>()
    this.items<ProtobufScopeItem> {
        result += it
    }
    return result.toTypedArray()
}

fun ProtobufStringValue.value(): String? {
    return stringLiteral.text?.trim('"')
}

fun ProtobufStringValue.stringRange(): TextRange {
    return stringRange(textRange)
}

fun ProtobufStringValue.stringRangeInParent(): TextRange {
    return stringRange(textRangeInParent)
}

private fun ProtobufStringValue.stringRange(relativelyRange: TextRange): TextRange {
    var textRange = relativelyRange
    val text = text

    if (textRange.length == 0) return textRange
    if (text.startsWith('"')) {
        textRange = TextRange.create(textRange.startOffset + 1, textRange.endOffset)
    }
    if (textRange.length == 0) return textRange
    if (text.endsWith('"')) {
        textRange = TextRange.create(textRange.startOffset, textRange.endOffset - 1)
    }
    return textRange
}

fun ProtobufNumberValue.float(): Double? {
    return floatLiteral?.text?.parseDoubleOrNull()
        ?: integerLiteral?.text?.parseLongOrNull()?.toDouble()
}

fun ProtobufNumberValue.int(): Long? {
    return integerLiteral?.text?.parseLongOrNull()
}

fun ProtobufNumberValue.uint(): Long? {
    return int()
}

fun ProtobufBooleanValue.value(): Boolean {
    return textMatches("true")
}

fun ProtobufRpcIO.stream(): Boolean {
    this.walkChildren<PsiElement>(false) {
        if (it.elementType is ProtobufKeywordToken && it.textMatches("stream")) {
            return true
        }
    }
    return false
}

fun ProtobufFieldLike.jsonName(): String? {
    if (this is ProtobufOptionOwner) {
        options("json_name").lastOrNull()?.value()?.stringValue?.value()?.let {
            return it
        }
    }
    return name()?.toCamelCase()
}
