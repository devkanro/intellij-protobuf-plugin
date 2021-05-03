package io.kanro.idea.plugin.protobuf.lang.psi

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
    this.fieldName?.let {
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
    val field = when (val parent = this.parent) {
        is ProtobufOptionName -> parent.extensionOptionName?.typeName?.reference?.resolve()
        is ProtobufFieldAssign -> {
            val messageValue = parent.parent as? ProtobufMessageValue ?: return null
            when (val assign = messageValue.parent.parent) {
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

fun ProtobufTypeName.absolutely(): Boolean {
    return firstChild !is ProtobufSymbolName
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

inline fun ProtobufScope.forEach(block: (ProtobufScopeItem) -> Unit) {
    this.items().forEach {
        if (it is ProtobufVirtualScope) {
            it.items().forEach(block)
        } else {
            block(it)
        }
    }
}

fun ProtobufScope.realItems(): Array<ProtobufScopeItem> {
    val result = mutableListOf<ProtobufScopeItem>()
    this.forEach {
        result += it
    }
    return result.toTypedArray()
}

fun ProtobufStringValue.value(): String? {
    return stringLiteral.text?.trim('"')
}

fun ProtobufNumberValue.float(): Double? {
    return when (val text = this.text.replace("""\s""".toRegex(), "")) {
        "nan", "-nan" -> Double.NaN
        "inf" -> Double.POSITIVE_INFINITY
        "-inf" -> Double.NEGATIVE_INFINITY
        else -> text.toDoubleOrNull()
    }
}

fun ProtobufNumberValue.int(): Long? {
    return text.toLongOrNull()
}

fun ProtobufNumberValue.uint(): ULong? {
    return text.toULongOrNull()
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
