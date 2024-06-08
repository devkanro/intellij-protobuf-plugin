package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.elementType
import io.kanro.idea.plugin.protobuf.lang.ProtoTextLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens
import io.kanro.idea.plugin.protobuf.lang.reference.ProtobufSymbolResolver
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver

interface ProtoTextFile :
    PsiFile,
    NamedElement,
    ItemPresentation {
    fun descriptorFile(): ProtobufFile? {
        val file =
            children.firstOrNull {
                it.elementType == ProtobufTokens.SHARP_LINE_COMMENT && it.text.startsWith(PROTOTEXT_HEADER_FILE)
            } ?: return null
        val filename = file.text.substringAfter(PROTOTEXT_HEADER_FILE).trim()

        return ProtobufRootResolver.findFile(filename, this).firstOrNull()?.let {
            PsiManager.getInstance(this.project).findFile(it) as? ProtobufFile
        }
    }

    fun message(): ProtobufMessageDefinition? {
        val file = descriptorFile() ?: return null
        val message =
            children.firstOrNull {
                it.elementType == ProtobufTokens.SHARP_LINE_COMMENT && it.text.startsWith(PROTOTEXT_HEADER_MESSAGE)
            } ?: return null
        val messageName = message.text.substringAfter(PROTOTEXT_HEADER_MESSAGE).trim()

        return ProtobufSymbolResolver.resolveInScope(
            file,
            QualifiedName.fromDottedString(messageName)
        ) as? ProtobufMessageDefinition
    }

    object Type : IFileElementType("PROTOTEXT_FILE", ProtoTextLanguage)

    companion object {
        const val PROTOTEXT_HEADER_FILE = "# proto-file:"
        const val PROTOTEXT_HEADER_MESSAGE = "# proto-message:"
    }
}
