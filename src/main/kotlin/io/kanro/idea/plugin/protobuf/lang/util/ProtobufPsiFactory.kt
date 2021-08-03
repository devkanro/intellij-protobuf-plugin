package io.kanro.idea.plugin.protobuf.lang.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiWhiteSpace
import io.kanro.idea.plugin.protobuf.lang.ProtobufFileType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.walkChildren

object ProtobufPsiFactory {
    fun createFile(project: Project, text: String): ProtobufFile {
        val name = "dummy.proto"
        return PsiFileFactory.getInstance(project)
            .createFileFromText(name, ProtobufFileType.INSTANCE, text) as ProtobufFile
    }

    fun createImport(project: Project, text: String): ProtobufImportStatement {
        return createFile(project, text).findChild() ?: throw IllegalStateException("Wrong import statement '$text'")
    }

    fun createTypeName(project: Project, text: String): ProtobufTypeName {
        createFile(project, "message Test { optional $text test = 1; }").walkChildren<ProtobufTypeName> {
            return it
        }
        throw IllegalStateException("Wrong type name '$text'")
    }

    fun createStringValue(project: Project, text: String): ProtobufStringValue {
        createFile(project, "import \"$text\";").walkChildren<ProtobufImportStatement> {
            return it.stringValue!!
        }
        throw IllegalStateException("Wrong type name '$text'")
    }

    fun createWhiteSpace(project: Project, text: String): PsiWhiteSpace {
        return createFile(project, text).findChild() ?: throw IllegalStateException("Wrong white space '$text'")
    }
}
