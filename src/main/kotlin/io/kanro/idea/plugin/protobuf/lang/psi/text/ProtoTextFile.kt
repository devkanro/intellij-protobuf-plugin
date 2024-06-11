package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import io.kanro.idea.plugin.protobuf.lang.ProtoTextLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.feature.NamedElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.text.impl.ProtoTextSharpLineCommentImpl
import io.kanro.idea.plugin.protobuf.lang.psi.text.reference.ProtoTextHeaderFileReference
import io.kanro.idea.plugin.protobuf.lang.psi.text.reference.ProtoTextHeaderMessageReference
import io.kanro.idea.plugin.protobuf.lang.psi.value.MessageValue

interface ProtoTextFile :
    PsiFile,
    NamedElement,
    ItemPresentation,
    MessageValue {
    fun schemaFile(): ProtobufFile? {
        children.forEach {
            if (it is ProtoTextSharpLineCommentImpl) {
                when (val reference = it.reference) {
                    is ProtoTextHeaderFileReference -> return reference.resolve() as? ProtobufFile
                }
            }
        }
        return null
    }

    fun schema(): ProtobufMessageDefinition? {
        children.forEach {
            if (it is ProtoTextSharpLineCommentImpl) {
                when (val reference = it.reference) {
                    is ProtoTextHeaderMessageReference -> return reference.resolve() as? ProtobufMessageDefinition
                }
            }
        }
        return null
    }

    object Type : IFileElementType("PROTOTEXT_FILE", ProtoTextLanguage)

    companion object {
        const val PROTOTEXT_HEADER_FILE = "# proto-file:"
        const val PROTOTEXT_HEADER_MESSAGE = "# proto-message:"
    }
}
