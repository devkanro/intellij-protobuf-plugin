package io.kanro.idea.plugin.protobuf.buf.annotator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.util.DocumentUtil
import io.kanro.idea.plugin.protobuf.buf.project.BufFileManager
import io.kanro.idea.plugin.protobuf.buf.settings.BufSettings
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.relativeTo

class BufLintAnnotator : ExternalAnnotator<BufLintContext, BufLintResult>() {
    private val jackson = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)

    private val ignoredBufLintRules = setOf("IMPORT_USED", "COMPILE")

    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): BufLintContext? {
        if (hasErrors) return null
        val bufSettings = file.project.service<BufSettings>()
        val path = bufSettings.bufPath() ?: return null
        if (!Files.exists(path)) return null
        if (!Files.isExecutable(path)) return null
        val fileManager = file.project.service<BufFileManager>()
        val module = fileManager.findModuleFromPsiElement(file) ?: return null
        val moduleRoot = module.path?.let { Paths.get(it) } ?: return null
        return BufLintContext(editor, path, module, file.originalFile.virtualFile.toNioPath().relativeTo(moduleRoot))
    }

    override fun doAnnotate(collectedInfo: BufLintContext?): BufLintResult? {
        val context = collectedInfo ?: return null
        val command = GeneralCommandLine(
            context.bufExecutable.toString(),
            "lint",
            "--error-format=json",
            "--path=${context.file}"
        ).withWorkDirectory(context.module.path)
        val output = ExecUtil.execAndGetOutput(command)
        val lints = output.stdout.lines().mapNotNull {
            try {
                jackson.readValue<BufLintAnnotation>(it).takeIf { it.type !in ignoredBufLintRules }
            } catch (e: Exception) {
                null
            }
        }
        return BufLintResult(context.editor, lints)
    }

    override fun apply(file: PsiFile, annotationResult: BufLintResult?, holder: AnnotationHolder) {
        annotationResult ?: return
        annotationResult.lints.forEach {
            val startOffset =
                DocumentUtil.calculateOffset(annotationResult.editor.document, it.startLine - 1, it.startColumn - 1, 1)
            val endOffset =
                DocumentUtil.calculateOffset(annotationResult.editor.document, it.endLine - 1, it.endColumn - 1, 1)

            holder.newAnnotation(HighlightSeverity.WARNING, it.message)
                .range(TextRange.create(startOffset, endOffset))
                .highlightType(ProblemHighlightType.WEAK_WARNING)
                .create()
        }
    }
}

class BufLintContext(
    val editor: Editor,
    val bufExecutable: Path,
    val module: BufFileManager.State.Module,
    val file: Path
)

class BufLintResult(val editor: Editor, val lints: List<BufLintAnnotation>)

data class BufLintAnnotation(
    val path: String,
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int,
    val type: String,
    val message: String
)
