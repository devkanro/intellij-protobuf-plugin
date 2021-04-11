package io.kanro.idea.plugin.protobuf.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.stratify.ProtobufOptionOwner
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufScopeItem
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufVirtualScope
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolFilters
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolResolver
import io.kanro.idea.plugin.protobuf.lang.util.AnyElement

inline fun <reified T : PsiElement> PsiElement.findChild(): T? {
    var child: PsiElement? = this.firstChild ?: return null
    while (child != null) {
        if (child is T) return child
        child = child.nextSibling
    }
    return return null
}

inline fun <reified T : PsiElement> PsiElement.findChildren(): Array<T> {
    var child: PsiElement? = this.firstChild ?: return arrayOf()
    val result = mutableListOf<T>()
    while (child != null) {
        if (child is T) result += child
        child = child.nextSibling
    }
    return result.toTypedArray()
}

inline fun <reified T : PsiElement> PsiElement.walkChildren(noinline block: (T) -> Unit) {
    walkChildren(T::class.java, block)
}

fun <T : PsiElement> PsiElement.walkChildren(clazz: Class<*>, block: (T) -> Unit) {
    var child: PsiElement? = this.firstChild ?: return
    while (child != null) {
        if (clazz.isInstance(child)) block(child as T)
        child.walkChildren(clazz, block)
        child = child.nextSibling
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

fun ProtobufOptionName.field(): ProtobufDefinition? {
    this.fieldName?.let {
        return it.reference?.resolve() as? ProtobufDefinition
    }
    this.extensionOptionName?.let {
        return it.typeName?.resolve() as? ProtobufDefinition
    }
    this.builtInOptionName?.let {
        return it.reference?.resolve() as? ProtobufDefinition
    }
    return null
}

fun ProtobufFieldName.message(): ProtobufScope? {
    val field = when (val parent = this.parent) {
        is ProtobufOptionName -> parent.extensionOptionName?.typeName?.resolve()
        is ProtobufFieldAssign -> {
            val messageValue = parent.parent as? ProtobufMessageValue ?: return null
            when (val assign = messageValue.parent) {
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
        is ProtobufFieldDefinition -> field.typeName.resolve() as? ProtobufMessageDefinition
        else -> null
    }
}

fun ProtobufBuiltInOptionName.isFieldDefaultOption(): Boolean {
    return this.textMatches("default") && parentOfType<ProtobufOptionOwner>() is ProtobufFieldDefinition
}

fun ProtobufTypeName.absolutely(): Boolean {
    return firstChild !is ProtobufSymbolName
}

fun ProtobufTypeName.resolve(): PsiElement? {
    return this.reference?.resolve()
}

private fun ProtobufTypeName.tryResolve(): Pair<QualifiedName?, PsiElement?> {
    return CachedValuesManager.getCachedValue(this) {
        val filter = when (parent) {
            is ProtobufExtensionOptionName -> ProtobufSymbolFilters.extensionOptionName(parentOfType())
            is ProtobufFieldDefinition,
            is ProtobufMapFieldDefinition -> ProtobufSymbolFilters.fieldTypeName
            is ProtobufRpcIO -> ProtobufSymbolFilters.rpcTypeName
            is ProtobufExtendDefinition -> ProtobufSymbolFilters.extendTypeName
            else -> AnyElement
        }
        var name = QualifiedName.fromComponents(this.symbolNameList.map { it.text })
        while (name.componentCount > 0) {
            val result = if (absolutely()) {
                ProtobufSymbolResolver.resolveAbsolutely(this, name, filter)
            } else {
                ProtobufSymbolResolver.resolveRelatively(this, name, filter)
            }
            if (result != null) {
                return@getCachedValue CachedValueProvider.Result.create(
                    name to result,
                    this
                )
            }
            name = name.removeLastComponent()
        }
        CachedValueProvider.Result.create(null to null, PsiModificationTracker.MODIFICATION_COUNT)
    }
}

fun ProtobufEnumValue.enum(): ProtobufEnumDefinition? {
    val field = when (val parent = this.parent) {
        is ProtobufOptionAssign -> {
            parent.optionName.field() as? ProtobufFieldDefinition
        }
        is ProtobufFieldAssign -> {
            parent.fieldName.reference?.resolve() as? ProtobufFieldDefinition
        }
        else -> null
    } ?: return null
    return field.typeName.resolve() as? ProtobufEnumDefinition
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
