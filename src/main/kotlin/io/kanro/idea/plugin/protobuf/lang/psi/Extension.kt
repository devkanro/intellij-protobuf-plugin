package io.kanro.idea.plugin.protobuf.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ValueAssign
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufFieldLike
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScopeItem
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScopeItemContainer
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufVirtualScope
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFile
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufKeywordToken
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens
import io.kanro.idea.plugin.protobuf.lang.psi.value.decodeStringFromStringLiteral
import io.kanro.idea.plugin.protobuf.lang.support.WellknownTypes
import io.kanro.idea.plugin.protobuf.string.parseDoubleOrNull
import io.kanro.idea.plugin.protobuf.string.parseLongOrNull
import io.kanro.idea.plugin.protobuf.string.toCamelCase
import java.util.Stack

fun PsiElement.firstLeaf(): PsiElement {
    var leaf = this
    while (leaf.firstChild != null) {
        leaf = leaf.firstChild
    }
    return leaf
}

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

inline fun <reified T : PsiElement> PsiElement.walkChildren(
    childTree: Boolean = true,
    block: (T) -> Unit,
) {
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

inline fun PsiElement.walkChildren(
    filter: PsiElementFilter,
    childTree: Boolean = true,
    block: (PsiElement) -> Unit,
) {
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
    return next {
        it is T
    } as? T
}

inline fun <reified T : PsiElement> PsiElement.prev(): T? {
    return prev {
        it is T
    } as? T
}

fun PsiElement.next(condition: (PsiElement) -> Boolean): PsiElement? {
    forEachNext {
        if (condition(it)) return it
    }
    return null
}

fun PsiElement.prev(condition: (PsiElement) -> Boolean): PsiElement? {
    forEachPrev {
        if (condition(it)) return it
    }
    return null
}

inline fun PsiElement.forEachNext(block: (PsiElement) -> Unit) {
    var next: PsiElement? = this.nextSibling ?: return
    while (next != null) {
        block(next)
        next = next.nextSibling
    }
}

inline fun PsiElement.forEachPrev(block: (PsiElement) -> Unit) {
    var prev: PsiElement? = this.prevSibling ?: return
    while (prev != null) {
        block(prev)
        prev = prev.prevSibling
    }
}

fun ProtobufOptionName.field(): ProtobufFieldLike? {
    this.optionFieldNameList.lastOrNull()?.let {
        return it.reference?.resolve() as? ProtobufFieldLike
    }
    return null
}

fun ProtobufOptionFieldName.message(): ProtobufScope? {
    return when (val field = reference?.resolve() as? ProtobufFieldLike) {
        is ProtobufGroupDefinition -> field
        is ProtobufFieldDefinition -> field.typeName.reference?.resolve() as? ProtobufMessageDefinition
        else -> null
    }
}

fun ProtobufOptionFieldName.isExtension(): Boolean {
    return this.extensionFieldName != null
}

fun ProtobufFieldName.message(): ProtobufScope? {
    val assign = parent as? ProtobufFieldAssign ?: return null
    val message = assign.parent as? ProtobufTextMessage ?: return null
    val parent = message.parent ?: return null

    if (parent is ProtoTextFile) {
        return parent.message()
    } else {
        val parentAssign = message.parentOfType<ValueAssign>() ?: return null

        if (parentAssign is ProtobufFieldAssign && parentAssign.fieldName.anyFieldName != null) {
            return parentAssign.fieldName.reference?.resolve() as? ProtobufScope
        }

        return when (val field = parentAssign.field()) {
            is ProtobufGroupDefinition -> field
            is ProtobufMapFieldDefinition -> field
            is ProtobufFieldDefinition -> field.typeName.reference?.resolve() as? ProtobufMessageDefinition
            else -> null
        }
    }
}

fun ProtobufOptionName.isFieldDefaultOption(): Boolean {
    return this.textMatches("default") && parentOfType<ProtobufOptionOwner>() is ProtobufFieldDefinition
}

fun ProtobufOptionName.isFieldJsonNameOption(): Boolean {
    return this.textMatches("json_name") && parentOfType<ProtobufOptionOwner>() is ProtobufFieldDefinition
}

fun ProtobufOptionFieldName.isFieldDefaultOption(): Boolean {
    return this.textMatches("default") && parentOfType<ProtobufOptionOwner>() is ProtobufFieldDefinition
}

fun ProtobufOptionFieldName.isFieldJsonNameOption(): Boolean {
    return this.textMatches("json_name") && parentOfType<ProtobufOptionOwner>() is ProtobufFieldDefinition
}

fun ProtobufExtensionFieldName.absolutely(): Boolean {
    return this.firstChild !is ProtobufSymbolName
}

fun ProtobufOptionFieldName.absolutely(): Boolean {
    return this.extensionFieldName?.absolutely() == true
}

fun ProtobufReservedName.name(): String {
    return if (firstChild.elementType == ProtobufTokens.STRING_LITERAL) {
        decodeStringFromStringLiteral(firstChild)
    } else {
        text
    }
}

fun ProtobufEnumValue.enum(): ProtobufEnumDefinition? {
    val assign = parentOfType<ValueAssign>() ?: return null
    val field = assign.field() ?: return null

    return when (field) {
        is ProtobufFieldDefinition -> {
            field.typeName.reference?.resolve() as? ProtobufEnumDefinition
        }
        is ProtobufMapFieldDefinition -> {
            val targetField = assign.findChild<ProtobufFieldName>()?.text ?: return null
            when (targetField) {
                "key" -> field.key()?.reference?.resolve() as? ProtobufEnumDefinition
                "value" -> field.value()?.reference?.resolve() as? ProtobufEnumDefinition
                else -> null
            }
        }
        else -> null
    }
}

fun ProtobufReservedRange.range(): LongRange? {
    val numbers = integerValueList.map { it.text.toLong() }
    return when (numbers.size) {
        1 ->
            if (lastChild.textMatches("max")) {
                LongRange(numbers[0], Long.MAX_VALUE)
            } else {
                LongRange(numbers[0], numbers[0])
            }

        2 -> LongRange(numbers[0], numbers[1])
        else -> null
    }
}

fun ProtobufExtensionRange.range(): LongRange? {
    val numbers = integerValueList.map { it.text.toLong() }
    return when (numbers.size) {
        1 ->
            if (lastChild.textMatches("max")) {
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

inline fun <reified T : ProtobufScopeItem> ProtobufScope.firstItemOrNull(block: (T) -> Boolean): T? {
    this.items().forEach {
        when (it) {
            is ProtobufVirtualScope -> {
                it.items().firstOrNull {
                    it is T && block(it)
                }?.let { return it as T }
            }

            is T -> {
                if (block(it)) return it
            }
        }
    }
    return null
}

inline fun <reified T : ProtobufScopeItem> ProtobufScope.filterItem(block: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    this.items().forEach {
        when (it) {
            is ProtobufVirtualScope -> {
                it.items().forEach {
                    if (it is T && block(it)) {
                        result += it
                    }
                }
            }

            is T -> {
                if (block(it)) {
                    result += it
                }
            }
        }
    }
    return result
}

fun ProtobufScope.realItems(): Array<ProtobufScopeItem> {
    val result = mutableListOf<ProtobufScopeItem>()
    this.items<ProtobufScopeItem> {
        result += it
    }
    return result.toTypedArray()
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
    return CachedValuesManager.getCachedValue(this) {
        val option = (this as? ProtobufOptionOwner)?.options("json_name")?.lastOrNull()
        val result =
            option?.value()?.toString()
                ?: name()?.toCamelCase()
        CachedValueProvider.Result.create(
            result,
            PsiModificationTracker.MODIFICATION_COUNT,
        )
    }
}

/**
 * Resolve type of qualified field for a message definition.
 * It could be returning a [ProtobufMessageDefinition] for message field.
 * returning a [ProtobufEnumDefinition] for enum field.
 * returning a [ProtobufMapFieldDefinition] for map field.
 * returning a [ProtobufGroupDefinition] for group field.
 */
fun ProtobufMessageDefinition.resolveFieldType(
    qualifiedName: QualifiedName,
    jsonSpec: Boolean = false,
): ProtobufElement? {
    if (qualifiedName.components.isEmpty()) return this

    val q =
        Stack<String>().apply {
            addAll(qualifiedName.components.asReversed())
        }

    var scope: ProtobufScope = this

    while (q.isNotEmpty()) {
        val field = q.pop()

        if (jsonSpec) {
            if (scope is ProtobufMessageDefinition) {
                val message = scope.qualifiedName().toString()
                if (message == WellknownTypes.ANY && field == "@type") {
                    return scope.firstItemOrNull<ProtobufFieldLike> {
                        it.name() == "type_url"
                    }
                } else if (message in WellknownTypes.types) {
                    return null
                }
            }
        }

        val fieldDefinition =
            scope.firstItemOrNull<ProtobufFieldLike> {
                it.name() == field || it.jsonName() == field
            } ?: return null

        when (fieldDefinition) {
            is ProtobufFieldDefinition -> {
                val type = fieldDefinition.typeName.reference?.resolve() as? ProtobufElement ?: return null
                if (q.isEmpty()) return type
                scope = type as? ProtobufMessageDefinition ?: return null
            }

            is ProtobufMapFieldDefinition -> {
                if (q.isEmpty()) return fieldDefinition
                q.pop()
                val type =
                    fieldDefinition.typeNameList.lastOrNull()?.reference?.resolve() as? ProtobufElement
                        ?: return null
                if (q.isEmpty()) return type
                scope = type as? ProtobufMessageDefinition ?: return null
            }

            is ProtobufGroupDefinition -> {
                scope = fieldDefinition
                if (q.isEmpty()) return scope
            }
        }
    }
    return null
}

/**
 * Resolve qualified field for a message definition.
 * It could be returning any [ProtobufFieldLike].
 */
fun ProtobufMessageDefinition.resolveField(
    qualifiedName: QualifiedName,
    jsonSpec: Boolean = false,
): ProtobufFieldLike? {
    val field = qualifiedName.lastComponent ?: return null
    val parentField = qualifiedName.removeTail(1)
    val parentType = resolveFieldType(parentField, jsonSpec) ?: return null

    if (jsonSpec) {
        if (parentType is ProtobufMessageDefinition) {
            val message = parentType.qualifiedName().toString()
            if (message == WellknownTypes.ANY && field == "@type") {
                return parentType.firstItemOrNull<ProtobufFieldLike> {
                    it.name() == "type_url"
                }
            } else if (message in WellknownTypes.types) {
                return null
            }
        }
    }

    return when (parentType) {
        is ProtobufMessageDefinition -> {
            parentType.firstItemOrNull { it.name() == field || it.jsonName() == field }
        }

        is ProtobufGroupDefinition -> {
            parentType.firstItemOrNull { it.name() == field || it.jsonName() == field }
        }

        else -> null
    }
}

fun ProtobufFieldDefinition.repeated(): Boolean {
    return this.fieldLabel?.textMatches("repeated") == true
}

fun ProtobufGroupDefinition.repeated(): Boolean {
    return this.fieldLabel?.textMatches("repeated") == true
}

fun ProtobufFieldDefinition.required(): Boolean {
    return this.fieldLabel?.textMatches("required") == true
}

fun ProtobufGroupDefinition.required(): Boolean {
    return this.fieldLabel?.textMatches("required") == true
}

fun ProtobufFieldDefinition.optional(): Boolean {
    return this.fieldLabel?.textMatches("optional") == true
}

fun ProtobufGroupDefinition.optional(): Boolean {
    return this.fieldLabel?.textMatches("optional") == true
}

fun ProtobufMapFieldDefinition.key(): ProtobufTypeName? {
    if (typeNameList.size < 2) return null
    return typeNameList[0]
}

fun ProtobufMapFieldDefinition.value(): ProtobufTypeName? {
    if (typeNameList.size < 2) return null
    return typeNameList[1]
}

fun <T> nullCachedValue(): CachedValueProvider.Result<T?> =
    CachedValueProvider.Result.create(null, PsiModificationTracker.MODIFICATION_COUNT)
