package io.kanro.idea.plugin.protobuf.lang.psi.impl

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.lang.ProtobufFileType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufOptionAssign
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufPackageStatement
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufReservedName
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufSyntaxStatement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufDefinitionContributor
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufOptionHover

class ProtobufFileImpl(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ProtobufLanguage), ProtobufFile {
    override fun getFileType(): FileType {
        return ProtobufFileType
    }

    override fun messages(): Iterable<ProtobufMessageDefinition> {
        return findChildrenByClass(ProtobufMessageDefinition::class.java).asIterable()
    }

    override fun enums(): Iterable<ProtobufEnumDefinition> {
        return findChildrenByClass(ProtobufEnumDefinition::class.java).asIterable()
    }

    override fun services(): Iterable<ProtobufServiceDefinition> {
        return findChildrenByClass(ProtobufServiceDefinition::class.java).asIterable()
    }

    override fun imports(): Iterable<ProtobufImportStatement> {
        return findChildrenByClass(ProtobufImportStatement::class.java).asIterable()
    }

    override fun toString(): String {
        return "Protobuf File"
    }

    override fun name(): String {
        return this.name
    }

    override fun scope(): QualifiedName? {
        return QualifiedName.fromComponents(packageParts().map { it.text })
    }

    override fun syntax(): String? {
        val syntax = this.findChildByClass(ProtobufSyntaxStatement::class.java) ?: return null
        val text = syntax.stringValue?.text ?: return null
        return text.substring(1, text.length - 1)
    }

    override fun packageParts(): Array<ProtobufPackageName> {
        return findChildByClass(ProtobufPackageStatement::class.java)?.packageNameList?.toTypedArray() ?: arrayOf()
    }

    override fun options(): Array<ProtobufOptionAssign> {
        return this.findChildrenByClass(ProtobufOptionHover::class.java).mapNotNull {
            it.option()
        }.toTypedArray()
    }

    override fun definitions(): Array<ProtobufDefinition> {
        return this.findChildrenByClass(ProtobufDefinitionContributor::class.java).flatMap {
            it.definitions().asIterable()
        }.toTypedArray() + this.findChildrenByClass(ProtobufDefinition::class.java)
    }

    override fun reservedNames(): Array<ProtobufReservedName> {
        return arrayOf()
    }
}
