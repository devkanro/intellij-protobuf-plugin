package io.kanro.idea.plugin.protobuf.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScopeItem
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScopeItemContainer
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufVirtualScope
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

fun <T> nullCachedValue(): CachedValueProvider.Result<T?> =
    CachedValueProvider.Result.create(null, PsiModificationTracker.MODIFICATION_COUNT)
