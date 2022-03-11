package io.kanro.idea.plugin.protobuf.aip.quickfix

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.codeInspection.HintAction
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.StubIndex
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufStringValue
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.feature.ProtobufSymbolReferenceHost
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.ResourceTypeIndex
import io.kanro.idea.plugin.protobuf.lang.psi.value
import io.kanro.idea.plugin.protobuf.lang.quickfix.ProtobufAddImportAction
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootResolver

class AddResourceImportFix(
    private val host: ProtobufStringValue
) : BaseIntentionAction(),
    HintAction,
    HighPriorityAction {

    private lateinit var elements: Array<ProtobufDefinition>

    override fun getFamilyName(): String {
        return "Import"
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        if (!host.isValid) return false
        val resourceType = host.value() ?: return false
        this.elements = StubIndex.getElements(
            ResourceTypeIndex.key, resourceType,
            project, ProtobufRootResolver.searchScope(host),
            ProtobufElement::class.java
        ).filterIsInstance<ProtobufDefinition>().toTypedArray()

        return elements.isNotEmpty()
    }

    override fun invoke(project: Project, editor: Editor, file: PsiFile?) {
        CommandProcessor.getInstance().runUndoTransparentAction {
            createAction(project, editor, host, elements).execute()
        }
    }

    private fun createAction(
        project: Project,
        editor: Editor,
        host: ProtobufSymbolReferenceHost,
        elements: Array<ProtobufDefinition>
    ): ProtobufAddImportAction {
        return ProtobufAddImportAction(project, editor, host, null, elements)
    }

    override fun getText(): String {
        return if (elements.size == 1) {
            "Import from \"${elements[0].importPath(host.file())}\""
        } else {
            "Import from \"${elements[0].importPath(host.file())}\" or other ${elements.size - 1} files"
        }
    }

    override fun showHint(editor: Editor): Boolean {
        return createAction(editor.project ?: return false, editor, host, elements).showHint()
    }
}
