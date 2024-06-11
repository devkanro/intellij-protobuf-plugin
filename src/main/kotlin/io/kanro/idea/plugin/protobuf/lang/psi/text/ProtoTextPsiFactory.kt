package io.kanro.idea.plugin.protobuf.lang.psi.text

import com.bybutter.sisyphus.string.escape
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import io.kanro.idea.plugin.protobuf.lang.ProtoTextFileType
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufStringValue
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

    fun createTypeName(
        project: Project,
        text: String,
    ): ProtoTextTypeName {
        ProtobufPsiFactory.createFile(project, "[$text] = 1;").walkChildren<ProtoTextTypeName> {
            return it
        }
        throw IllegalStateException("Wrong type name '$text'")
    }

    fun createStringValue(
        project: Project,
        text: String,
    ): ProtobufStringValue {
        ProtobufPsiFactory.createFile(project, "a = \"${text.escape()}\";").walkChildren<ProtobufStringValue> {
            return it
        }
        throw IllegalStateException("Wrong string value '$text'")
    }
}
