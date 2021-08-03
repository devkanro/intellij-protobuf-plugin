package io.kanro.idea.plugin.protobuf.lang.annotator

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypes
import io.kanro.idea.plugin.protobuf.lang.psi.findChildren
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufFileReferenceContributor
import io.kanro.idea.plugin.protobuf.lang.psi.resolve
import io.kanro.idea.plugin.protobuf.lang.psi.walkChildren
import io.kanro.idea.plugin.protobuf.lang.quickfix.OptimizeImportsFix
import io.kanro.idea.plugin.protobuf.lang.util.asFilter

open class FileTracker(file: ProtobufFile) {
    private val imported = mutableMapOf<String, MutableSet<ProtobufImportStatement>>()
    private val fileReference = mutableMapOf<ProtobufFile, Int>()
    private val packageStatements = mutableListOf<ProtobufPackageStatement>()

    init {
        file.imports().forEach { record(it) }
        packageStatements += file.findChildren()
        file.walkChildren<ProtobufFileReferenceContributor> {
            record(it)
        }
    }

    open fun visit(statement: ProtobufImportStatement, holder: AnnotationHolder) {
        val file = statement.stringValue?.stringLiteral?.text?.trim('"') ?: return
        if ((imported[file]?.size ?: 0) > 1) {
            createDuplicate(statement, file, holder)
            return
        }
        val resolvedFile = statement.resolve() ?: return createUnknown(statement, file, holder)
        if (statement.importLabel?.textMatches("public") != true && fileReference.getOrDefault(resolvedFile, 0) == 0) {
            createUnused(statement, file, holder)
        }
    }

    open fun visit(statement: ProtobufPackageStatement, holder: AnnotationHolder) {
        if (packageStatements.size > 1) {
            createDuplicate(statement, holder)
        }
    }

    open fun isUnused(file: ProtobufFile): Boolean {
        return fileReference.getOrDefault(file, 0) == 0
    }

    protected open fun record(statement: ProtobufImportStatement) {
        val file = statement.stringValue?.stringLiteral?.text?.trim('"') ?: return
        imported.getOrPut(file) {
            mutableSetOf()
        }.add(statement)
    }

    protected open fun record(typeName: ProtobufFileReferenceContributor) {
        val file = typeName.reference?.resolve()?.containingFile as? ProtobufFile ?: return
        fileReference[file] = fileReference.getOrDefault(file, 0) + 1
    }

    protected open fun createDuplicate(statement: ProtobufPackageStatement, holder: AnnotationHolder) {
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            "Duplicate package statement: \"${statement.packageNameList.joinToString(".") { it.text }}\""
        ).range(statement.textRange).create()
    }

    protected open fun createDuplicate(statement: ProtobufImportStatement, file: String, holder: AnnotationHolder) {
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            "Duplicate import: \"$file\""
        ).range(statement.stringValue?.textRange ?: statement.textRange).create()
    }

    protected open fun createUnknown(statement: ProtobufImportStatement, file: String, holder: AnnotationHolder) {
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            "Imported file \"$file\" not found"
        ).highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
            .range(statement.stringValue?.textRange ?: statement.textRange).create()
    }

    protected open fun createUnused(statement: ProtobufImportStatement, message: String, holder: AnnotationHolder) {
        holder.newAnnotation(
            HighlightSeverity.INFORMATION,
            "Imported file not be used"
        ).highlightType(ProblemHighlightType.LIKE_UNUSED_SYMBOL)
            .range(statement.stringValue?.textRange ?: statement.textRange)
            .withFix(OptimizeImportsFix())
            .create()
    }

    companion object {
        private val importUsageFilter = TokenSet.create(ProtobufTypes.TYPE_NAME, ProtobufTypes.STRING_VALUE).asFilter()

        fun tracker(file: ProtobufFile): FileTracker {
            return CachedValuesManager.getCachedValue(file) {
                CachedValueProvider.Result.create(
                    FileTracker(file), PsiModificationTracker.MODIFICATION_COUNT
                )
            }
        }
    }
}
