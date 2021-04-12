package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.codeInsight.completion.DeclarativeInsertHandler
import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import io.kanro.idea.plugin.protobuf.Icons
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.file.FileResolver
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement

class ProtobufImportReference(import: ProtobufImportStatement) : PsiReferenceBase<ProtobufImportStatement>(import) {
    private object Resolver : ResolveCache.Resolver {
        override fun resolve(ref: PsiReference, incompleteCode: Boolean): PsiElement? {
            return resolve(ref.element as ProtobufImportStatement)
        }
    }

    override fun resolve(): PsiElement? {
        return ResolveCache.getInstance(element.project)
            .resolveWithCaching(this, Resolver, false, false)
    }

    override fun calculateDefaultRangeInElement(): TextRange {
        val string = element.stringValue ?: return TextRange.EMPTY_RANGE
        return TextRange.create(string.startOffsetInParent + 1, string.startOffsetInParent + string.textLength - 1)
    }

    override fun getVariants(): Array<Any> {
        val imported = element.stringValue?.text?.trim('"') ?: return arrayOf()
        val parent = imported.substringBeforeLast('/', ".")
        return FileResolver.collectProtobuf(parent, element).map {
            fileLookup(parent, it)
        }.toTypedArray()
    }

    companion object {
        private fun fileLookup(parent: String, file: VirtualFile): LookupElement {
            val completionText = if (parent == ".") {
                file.name
            } else {
                "$parent/${file.name}"
            }

            return if (file.isDirectory) {
                LookupElementBuilder.create(completionText)
                    .withTypeText("directory")
                    .withIcon(Icons.FOLDER)
                    .withPresentableText(file.name)
                    .withInsertHandler { context, item ->
                        nextImportInsert.handleInsert(context, item)
                    }
                    .withAutoCompletionPolicy(AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE)
            } else {
                LookupElementBuilder.create(completionText)
                    .withTypeText("proto")
                    .withIcon(Icons.FILE)
                    .withPresentableText(file.name)
                    .withInsertHandler(completeImportInsert)
            }
        }

        private val completeImportInsert = SmartInsertHandler("\";")

        private val nextImportInsert = DeclarativeInsertHandler.Builder()
            .insertOrMove("/")
            .triggerAutoPopup()
            .build()

        fun resolve(element: ProtobufImportStatement): PsiElement? {
            val filePath = element.stringValue!!.text
            val file =
                FileResolver.resolveFile(filePath.substring(1, filePath.length - 1), element).firstOrNull()
                    ?: return null
            return PsiManager.getInstance(element.project).findFile(file)
        }
    }
}
