package io.kanro.idea.plugin.protobuf.lang.util

import com.intellij.psi.util.QualifiedName

fun String.toQualifiedName(): QualifiedName {
    return QualifiedName.fromDottedString(this)
}
