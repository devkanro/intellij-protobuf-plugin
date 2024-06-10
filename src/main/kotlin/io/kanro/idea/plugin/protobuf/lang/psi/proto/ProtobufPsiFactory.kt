package io.kanro.idea.plugin.protobuf.lang.psi.proto

import com.bybutter.sisyphus.string.escape
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiWhiteSpace
import io.kanro.idea.plugin.protobuf.lang.ProtobufFileType
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.walkChildren

object ProtobufPsiFactory {
    fun createFile(
        project: Project,
        text: String,
    ): ProtobufFile {
        val name = "dummy.proto"
        return PsiFileFactory.getInstance(project)
            .createFileFromText(name, ProtobufFileType.INSTANCE, text) as ProtobufFile
    }

    fun createImport(
        project: Project,
        text: String,
    ): ProtobufImportStatement {
        return createFile(project, text).findChild() ?: throw IllegalStateException("Wrong import statement '$text'")
    }

    fun createTypeName(
        project: Project,
        text: String,
    ): ProtobufTypeName {
        createFile(project, "message Test { optional $text test = 1; }").walkChildren<ProtobufTypeName> {
            return it
        }
        throw IllegalStateException("Wrong type name '$text'")
    }

    fun createExtensionFieldName(
        project: Project,
        text: String,
    ): ProtobufExtensionFieldName {
        createFile(project, "option ($text) = 1;").walkChildren<ProtobufExtensionFieldName> {
            return it
        }
        throw IllegalStateException("Wrong extension field name '$text'")
    }

    fun createFieldName(
        project: Project,
        text: String,
    ): ProtobufFieldName {
        createFile(project, "option test = { $text : 1 };").walkChildren<ProtobufFieldName> {
            return it
        }
        throw IllegalStateException("Wrong extension field name '$text'")
    }

    fun createStringValue(
        project: Project,
        text: String,
    ): ProtobufStringValue {
        createFile(project, "import \"${text.escape()}\";").walkChildren<ProtobufImportStatement> {
            return it.stringValue!!
        }
        throw IllegalStateException("Wrong type name '$text'")
    }

    fun createWhiteSpace(
        project: Project,
        text: String,
    ): PsiWhiteSpace {
        return createFile(project, text).findChild() ?: throw IllegalStateException("Wrong white space '$text'")
    }
}
