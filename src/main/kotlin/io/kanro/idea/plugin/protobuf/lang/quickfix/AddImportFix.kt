package io.kanro.idea.plugin.protobuf.lang.quickfix

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.codeInspection.HintAction
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.StubIndex
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufSymbolReferenceHost
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufSymbolReferenceHover
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.ShortNameIndex
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver
import io.kanro.idea.plugin.protobuf.lang.util.matchesSuffix

class AddImportFix(
    private val host: ProtobufSymbolReferenceHost
) : BaseIntentionAction(),
    HintAction,
    HighPriorityAction {

    private lateinit var elements: Array<ProtobufDefinition>
    private lateinit var hover: ProtobufSymbolReferenceHover

    override fun getFamilyName(): String {
        return "Import"
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        if (!host.isValid) return false
        hover = host.referencesHover() ?: return false
        val parts = hover.symbolParts()
        val name = hover.symbol()
        if (hover.absolutely() || parts.size > 1) {
            this.elements = StubIndex.getElements(
                ShortNameIndex.key, name.lastComponent!!,
                project, ProtobufRootResolver.searchScope(host),
                ProtobufElement::class.java
            ).filterIsInstance<ProtobufDefinition>().filter {
                it.qualifiedName()?.matchesSuffix(name) == true
            }.toTypedArray()
        } else {
            this.elements = StubIndex.getElements(
                ShortNameIndex.key, name.lastComponent!!,
                project, ProtobufRootResolver.searchScope(host),
                ProtobufElement::class.java
            ).filterIsInstance<ProtobufDefinition>().toTypedArray()
        }

        return elements.isNotEmpty()
    }

    override fun invoke(project: Project, editor: Editor, file: PsiFile?) {
        CommandProcessor.getInstance().runUndoTransparentAction {
            createAction(project, editor, host, hover, elements).execute()
        }
    }

    private fun createAction(
        project: Project,
        editor: Editor,
        host: ProtobufSymbolReferenceHost,
        hover: ProtobufSymbolReferenceHover,
        elements: Array<ProtobufDefinition>
    ): ProtobufAddImportAction {
        return ProtobufAddImportAction(project, editor, host, hover, elements)
    }

    override fun getText(): String {
        return if (elements.size == 1) {
            "Import from \"${elements[0].importPath(host.file())}\""
        } else {
            "Import from \"${elements[0].importPath(host.file())}\" or other ${elements.size - 1} files"
        }
    }

    override fun showHint(editor: Editor): Boolean {
        return createAction(editor.project ?: return false, editor, host, hover, elements).showHint()
    }
}
