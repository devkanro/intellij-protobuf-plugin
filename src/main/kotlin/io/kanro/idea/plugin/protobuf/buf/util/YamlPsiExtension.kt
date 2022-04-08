package io.kanro.idea.plugin.protobuf.buf.util

import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.buf.schema.BufSchema
import io.kanro.idea.plugin.protobuf.buf.schema.bufSchema
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLPsiElement
import org.jetbrains.yaml.psi.YAMLSequence
import org.jetbrains.yaml.psi.YAMLSequenceItem

fun PsiElement.yamlParent(withSelf: Boolean = true): YAMLPsiElement? {
    return this.parentOfType(withSelf)
}

fun PsiElement.yamlPath(): QualifiedName? {
    var element: YAMLPsiElement = this.yamlParent() ?: return null
    val parts = mutableListOf<String>()

    while (element !is YAMLDocument) {
        when (element) {
            is YAMLKeyValue -> if (!element.keyText.contains(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) parts += element.keyText
            is YAMLSequenceItem -> parts += element.parentOfType<YAMLSequence>(false)?.items?.indexOf(element)
                ?.toString() ?: "?"
        }
        element = element.yamlParent(false) ?: return null
    }

    return QualifiedName.fromComponents(parts.reversed())
}

fun PsiElement.rootSchema(): BufSchema<out YAMLPsiElement>? {
    val name = containingFile?.originalFile?.virtualFile?.name ?: return null
    val document = parentOfType<YAMLDocument>(false) ?: return null
    val rootMapping = document.topLevelValue as? YAMLMapping
    val version = rootMapping?.keyValues?.firstOrNull { it.keyText == "version" }?.valueText?.takeIf {
        !it.contains(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)
    }
    return bufSchema(name, version)
}

fun PsiElement.elementSchema(): BufSchema<out YAMLPsiElement>? {
    val root = rootSchema() ?: return null
    return root.find(yamlPath() ?: return null)
}
