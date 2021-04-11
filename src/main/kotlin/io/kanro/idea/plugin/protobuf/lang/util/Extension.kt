package io.kanro.idea.plugin.protobuf.lang.util

import com.intellij.psi.util.QualifiedName

fun String.toQualifiedName(): QualifiedName {
    return QualifiedName.fromDottedString(this)
}

fun <T> List<T>.contentEquals(other: List<T>): Boolean {
    if (this.size != other.size) return false
    forEachIndexed { index, t ->
        if (t != other[index]) return false
    }
    return true
}
