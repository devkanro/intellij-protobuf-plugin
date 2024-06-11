package io.kanro.idea.plugin.protobuf.lang.quickfix

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.codeInspection.HintAction
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.StubIndex
import io.kanro.idea.plugin.protobuf.lang.psi.feature.ReferenceElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index.ShortNameIndex
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver
import io.kanro.idea.plugin.protobuf.lang.util.matchesSuffix

class AddImportFix(
    private val host: ReferenceElement,
) : BaseIntentionAction(), HintAction, HighPriorityAction {
    private lateinit var elements: Array<ProtobufDefinition>

    override fun getFamilyName(): String {
        return "Import"
    }

    override fun isAvailable(
        project: Project,
        editor: Editor,
        file: PsiFile,
    ): Boolean {
        if (!host.isValid) return false
        if (host.containingFile.originalFile !is ProtobufFile) return false
        val symbol = host.symbol() ?: return false
        if (symbol.componentCount > 1) {
            this.elements =
                StubIndex.getElements(
                    ShortNameIndex.key,
                    symbol.lastComponent!!,
                    project,
                    ProtobufRootResolver.searchScope(host),
                    ProtobufElement::class.java,
                ).filterIsInstance<ProtobufDefinition>().filter {
                    it.qualifiedName()?.matchesSuffix(symbol) == true
                }.toTypedArray()
        } else {
            this.elements =
                StubIndex.getElements(
                    ShortNameIndex.key,
                    symbol.lastComponent!!,
                    project,
                    ProtobufRootResolver.searchScope(host),
                    ProtobufElement::class.java,
                ).filterIsInstance<ProtobufDefinition>().toTypedArray()
        }

        return elements.isNotEmpty()
    }

    override fun invoke(
        project: Project,
        editor: Editor,
        file: PsiFile?,
    ) {
        CommandProcessor.getInstance().runUndoTransparentAction {
            createAction(project, editor, elements).execute()
        }
    }

    private fun createAction(
        project: Project,
        editor: Editor,
        elements: Array<ProtobufDefinition>,
    ): ProtobufAddImportAction {
        return ProtobufAddImportAction(project, editor, host, elements)
    }

    override fun getText(): String {
        val protobufFile = host.containingFile.originalFile as ProtobufFile
        return if (elements.size == 1) {
            "Import from \"${elements[0].importPath(protobufFile)}\""
        } else {
            "Import from \"${elements[0].importPath(protobufFile)}\" or other ${elements.size - 1} files"
        }
    }

    override fun showHint(editor: Editor): Boolean {
        return createAction(editor.project ?: return false, editor, elements).showHint()
    }
}
