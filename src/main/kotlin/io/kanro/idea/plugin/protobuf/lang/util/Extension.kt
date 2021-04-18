package io.kanro.idea.plugin.protobuf.lang.util

import com.intellij.psi.util.QualifiedName

fun String.toQualifiedName(): QualifiedName {
    return QualifiedName.fromDottedString(this)
}

fun QualifiedName.removeCommonPrefix(other: QualifiedName): QualifiedName {
    return removeCommonPrefix(other, 0)
}

private fun QualifiedName.removeCommonPrefix(other: QualifiedName, index: Int): QualifiedName {
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
