package io.kanro.idea.plugin.protobuf.lang.reference

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
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.stringRangeInParent
import io.kanro.idea.plugin.protobuf.lang.psi.value
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver
import io.kanro.idea.plugin.protobuf.lang.util.ProtobufPsiFactory

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
        return string.stringRangeInParent()
    }

    override fun getVariants(): Array<Any> {
        val imported = element.stringValue?.text?.trim('"') ?: return arrayOf()
        val parent = imported.substringBeforeLast('/', ".")
        return ProtobufRootResolver.collectProtobuf(parent, element).map {
            fileLookup(parent, it)
        }.toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val text = element.stringValue?.value() ?: return element
        val oldFile = resolve() ?: return element
        val oldName = oldFile.containingFile.virtualFile?.name ?: return element
        val newImport = text.replace(oldName, newElementName)
        element.stringValue?.replace(ProtobufPsiFactory.createStringValue(element.project, newImport))
        return element
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

        private val nextImportInsert = SmartInsertHandler("/", 0, true)

        fun resolve(element: ProtobufImportStatement): PsiElement? {
            val filePath = element.stringValue?.value() ?: return null
            val file = ProtobufRootResolver.findFile(filePath, element).firstOrNull() ?: return null
            return PsiManager.getInstance(element.project).findFile(file)
        }
    }
}
