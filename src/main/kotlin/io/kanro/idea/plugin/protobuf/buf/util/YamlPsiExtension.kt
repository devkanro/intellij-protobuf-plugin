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
    val name = containingFile.originalFile.virtualFile?.name ?: return null
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

fun isBufConfiguration(name: String): Boolean {
    return isBufYaml(name) || isBufGenYaml(name) || isBufWorkYaml(name) || isBufLock(name)
}

const val BUF_YAML = "buf.yaml"

const val BUF_YML = "buf.yml"

fun isBufYaml(name: String): Boolean {
    return when (name.lowercase()) {
        BUF_YAML, BUF_YML -> true
        else -> false
    }
}

const val BUF_GEN_YAML = "buf.gen.yaml"

const val BUF_GEN_YML = "buf.gen.yml"

fun isBufGenYaml(name: String): Boolean {
    return when (name.lowercase()) {
        BUF_GEN_YAML, BUF_GEN_YML -> true
        else -> false
    }
}

const val BUF_WORK_YAML = "buf.work.yaml"

const val BUF_WORK_YML = "buf.work.yml"

fun isBufWorkYaml(name: String): Boolean {
    return when (name.lowercase()) {
        BUF_WORK_YAML, BUF_WORK_YML -> true
        else -> false
    }
}

const val BUF_LOCK = "buf.lock"

fun isBufLock(name: String): Boolean {
    return when (name.lowercase()) {
        BUF_LOCK -> true
        else -> false
    }
}