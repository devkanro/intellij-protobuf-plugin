package io.kanro.idea.plugin.protobuf.lang.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.util.QualifiedName

fun String.toQualifiedName(): QualifiedName {
    return QualifiedName.fromDottedString(this)
}

fun QualifiedName.removeCommonPrefix(other: QualifiedName?): QualifiedName {
    other ?: return this
    var prefix = 0
    while (true) {
        if (prefix >= this.componentCount) break
        if (prefix >= other.componentCount) break

        if (this.components[prefix] == other.components[prefix]) {
            prefix++
        } else {
            break
        }
    }
    return this.removeHead(prefix)
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

fun <T> List<T>.contentEquals(other: List<T>): Boolean {
    if (this.size != other.size) return false
    forEachIndexed { index, t ->
        if (t != other[index]) return false
    }
    return true
}

val PsiElement.module: Module?
    get() = ModuleUtilCore.findModuleForPsiElement(this)
