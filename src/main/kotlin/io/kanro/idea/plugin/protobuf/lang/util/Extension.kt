package io.kanro.idea.plugin.protobuf.lang.util

import com.intellij.psi.util.QualifiedName

fun String.toQualifiedName(): QualifiedName {
    return QualifiedName.fromDottedString(this)
}

fun QualifiedName.removeCommonPrefix(other: QualifiedName?): QualifiedName {
    return removeCommonPrefix(other ?: return this, 0)
}

fun QualifiedName.matchesSuffix(suffix: QualifiedName): Boolean {
    if (componentCount < suffix.componentCount) {
        return false
    }
    for (i in 1..suffix.componentCount) {
        if (components[componentCount - i] != suffix.components[suffix.componentCount - i]) {
            return false
        }
    }
    return true
}

private fun QualifiedName.removeCommonPrefix(other: QualifiedName, index: Int): QualifiedName {
    if (index >= this.componentCount) return this.removeHead(index - 1)
    if (index >= other.componentCount) return this.removeHead(index - 1)
    if (this.components[index] == other.components[index]) {
        return removeCommonPrefix(other, index + 1)
    }
    return this.removeHead(index)
}

fun <T> List<T>.contentEquals(other: List<T>): Boolean {
    if (this.size != other.size) return false
    forEachIndexed { index, t ->
        if (t != other[index]) return false
    }
    return true
}
