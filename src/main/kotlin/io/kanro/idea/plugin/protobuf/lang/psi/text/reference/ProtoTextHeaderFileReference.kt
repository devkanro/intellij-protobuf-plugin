package io.kanro.idea.plugin.protobuf.lang.psi.text.reference

import com.intellij.codeInsight.lookup.AutoCompletionPolicy
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.lang.ProtobufFileType
import io.kanro.idea.plugin.protobuf.lang.completion.SmartInsertHandler
import io.kanro.idea.plugin.protobuf.lang.psi.text.ProtoTextFile
import io.kanro.idea.plugin.protobuf.lang.psi.text.impl.ProtoTextSharpLineCommentImpl
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver

class ProtoTextHeaderFileReference(comment: ProtoTextSharpLineCommentImpl) :
    PsiReferenceBase<ProtoTextSharpLineCommentImpl>(comment) {
    override fun resolve(): PsiElement? {
        val fileName = fileName()
        return if (fileName.startsWith(".")) {
            element.containingFile.originalFile.virtualFile.parent?.findFileByRelativePath(fileName)?.let {
                PsiManager.getInstance(element.project).findFile(it)
            }
        } else {
            ProtobufRootResolver.findFile(fileName, element).firstOrNull()?.let {
                PsiManager.getInstance(element.project).findFile(it)
            }
        }
    }

    override fun getRangeInElement(): TextRange {
        val text = element.text
        val fileName = fileName()
        val start = text.indexOf(fileName)
        return TextRange.create(start, start + fileName.length)
    }

    fun fileName(): String {
        return element.text.substringAfter(ProtoTextFile.PROTOTEXT_HEADER_FILE).trim()
    }

    override fun getVariants(): Array<Any> {
        val imported = fileName()
        val parent = imported.substringBeforeLast('/', "")
        return if (parent.startsWith(".")) {
            element.containingFile.originalFile.virtualFile?.parent?.findFileByRelativePath(parent)
                ?.children?.mapNotNull {
                    fileLookup(parent, it)
                }?.toTypedArray() ?: emptyArray()
        } else {
            ProtobufRootResolver.collectProtobuf(parent, element).mapNotNull {
                fileLookup(parent, it)
            }.toTypedArray()
        }
    }

    companion object {
        private val nextImportInsert = SmartInsertHandler("/", 0, true)

        private fun fileLookup(
            parent: String,
            file: VirtualFile,
        ): LookupElement? {
            val completionText =
                if (parent == "") {
                    file.name
                } else {
                    "$parent/${file.name}".trim('/')
                }

            return if (file.isDirectory) {
                LookupElementBuilder.create(completionText)
                    .withTypeText("directory")
                    .withIcon(ProtobufIcons.FOLDER)
                    .withPresentableText(file.name)
                    .withInsertHandler { context, item ->
                        nextImportInsert.handleInsert(context, item)
                    }
                    .withAutoCompletionPolicy(AutoCompletionPolicy.ALWAYS_AUTOCOMPLETE)
            } else {
                if (file.fileType !is ProtobufFileType) return null
                LookupElementBuilder.create(completionText)
                    .withTypeText("proto")
                    .withIcon(ProtobufIcons.FILE)
                    .withPresentableText(file.name)
            }
        }
    }
}
