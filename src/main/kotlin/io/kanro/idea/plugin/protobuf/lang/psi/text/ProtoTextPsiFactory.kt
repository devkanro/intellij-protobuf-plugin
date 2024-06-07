package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import io.kanro.idea.plugin.protobuf.lang.ProtoTextFileType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.walkChildren

object ProtoTextPsiFactory {
    fun createFile(
        project: Project,
        text: String,
    ): ProtoTextFile {
        val name = "dummy.txtpb"
        return PsiFileFactory.getInstance(project)
            .createFileFromText(name, ProtoTextFileType.INSTANCE, text) as ProtoTextFile
    }

    fun createFieldName(
        project: Project,
        text: String,
    ): ProtoTextFieldName {
        ProtobufPsiFactory.createFile(project, "$text = 1;").walkChildren<ProtoTextField> {
            return it.fieldName
        }
        throw IllegalStateException("Wrong type name '$text'")
    }
}
