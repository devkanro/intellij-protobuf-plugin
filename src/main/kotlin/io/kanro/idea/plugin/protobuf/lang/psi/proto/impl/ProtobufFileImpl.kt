package io.kanro.idea.plugin.protobuf.lang.psi.proto.impl

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.libraries.LibraryUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.QualifiedName
import io.kanro.idea.plugin.protobuf.ProtobufIcons
import io.kanro.idea.plugin.protobuf.aip.AipOptions
import io.kanro.idea.plugin.protobuf.lang.ProtobufFileType
import io.kanro.idea.plugin.protobuf.lang.ProtobufLanguage
import io.kanro.idea.plugin.protobuf.lang.psi.findChild
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEditionStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufEnumDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufImportStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufMessageDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPackageStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufPsiFactory
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufReservedName
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufServiceDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufSyntaxStatement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.feature.ProtobufOptionHover
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScope
import javax.swing.Icon

class ProtobufFileImpl(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ProtobufLanguage), ProtobufFile {
    override fun getFileType(): FileType {
        return ProtobufFileType.INSTANCE
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

    override fun lookup(name: String?): LookupElementBuilder? {
        return null
    }

    override fun getPresentableText(): String? {
        return name()
    }

    override fun name(): String {
        return this.name
    }

    override fun getPresentation(): ItemPresentation? {
        return this
    }

    override fun getLocationString(): String? {
        return scope()?.toString()
    }

    private fun getRootInfo(): String {
        return when (val system = virtualFile.fileSystem) {
            is ArchiveFileSystem -> {
                val project = ProjectLocator.getInstance().guessProjectForFile(virtualFile)
                if (project != null) {
                    LibraryUtil.findLibraryEntry(virtualFile, project)?.let {
                        return it.presentableName
                    }
                }
                "${system.getLocalByEntry(virtualFile)?.name}"
            }

            is LocalFileSystem -> {
                val project = ProjectLocator.getInstance().guessProjectForFile(virtualFile)
                if (project != null) {
                    ModuleManager.getInstance(project).modules.forEach {
                        if (ModuleRootManager.getInstance(it).fileIndex.isInContent(virtualFile)) {
                            return it.name
                        }
                    }
                    return project.name
                }
                "(external)"
            }

            else -> "(unsupported)"
        }
    }

    override fun owner(): ProtobufScope? {
        return null
    }

    override fun scope(): QualifiedName? {
        return QualifiedName.fromComponents(packageParts().map { it.text })
    }

    override fun syntax(): String? {
        val syntax = this.findChild<ProtobufSyntaxStatement>() ?: return null
        val text = syntax.stringValue?.value() ?: return null
        return text.substring(1, text.length - 1)
    }

    override fun edition(): String? {
        val edition = this.findChild<ProtobufEditionStatement>() ?: return null
        val text = edition.stringValue?.value() ?: return null
        return text.substring(1, text.length - 1)
    }

    override fun packageParts(): Array<ProtobufPackageName> {
        return findChild<ProtobufPackageStatement>()?.packageNameList?.toTypedArray() ?: arrayOf()
    }

    override fun resourceDefinitions(): Array<ProtobufOptionHover> {
        return options(AipOptions.resourceDefinitionOption)
    }

    override fun addImport(protobufElement: ProtobufElement): Boolean {
        return addImport(protobufElement.importPath(this) ?: return false)
    }

    override fun addImport(path: String): Boolean {
        if (this.importPath(this) == path) return false

        val imports = imports()
        imports.forEach {
            if (it.stringValue?.value() == path) return false
        }

        imports.forEach {
            val imported = it.stringValue?.value() ?: return@forEach
            if (path < imported) {
                val file = ProtobufPsiFactory.createFile(project, "import \"$path\";\n")
                addRangeBefore(file.firstChild, file.lastChild, it)
                return true
            }
        }

        imports.lastOrNull()?.let {
            val file = ProtobufPsiFactory.createFile(project, "\nimport \"$path\";")
            addRangeAfter(file.firstChild, file.lastChild, it)
            return true
        }

        findChild<ProtobufPackageStatement>()?.let {
            val file = ProtobufPsiFactory.createFile(project, "\n\nimport \"$path\";")
            addRangeAfter(file.firstChild, file.lastChild, it)
            return true
        }

        findChild<ProtobufSyntaxStatement>()?.let {
            val file = ProtobufPsiFactory.createFile(project, "\n\nimport \"$path\";")
            addRangeAfter(file.firstChild, file.lastChild, it)
            return true
        }

        val file = ProtobufPsiFactory.createFile(project, "import \"$path\";\n\n")
        addRangeBefore(file.firstChild, file.lastChild, firstChild)
        return true
    }

    override fun reservedNames(): Array<ProtobufReservedName> {
        return arrayOf()
    }

    override fun getIcon(unused: Boolean): Icon? {
        return ProtobufIcons.FILE
    }
}
