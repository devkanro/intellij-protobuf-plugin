package io.kanro.idea.plugin.protobuf.lang.quickfix

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInspection.HintAction
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.parentOfType
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufTypeName
import io.kanro.idea.plugin.protobuf.lang.psi.primitive.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.stub.index.QualifiedNameIndex

class AddImportFix() : PsiElementBaseIntentionAction(), HintAction, HighPriorityAction {
    init {
        text = "Import"
    }

    override fun getFamilyName(): String {
        return "Import"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        val typeName = element.parentOfType<ProtobufTypeName>() ?: return false
        val text = typeName.text
        val scope = GlobalSearchScope.allScope(project)
        val elements = StubIndex.getElements(
            QualifiedNameIndex.key, text,
            project, scope,
            ProtobufElement::class.java
        ).filterIsInstance<NavigationItem>().toTypedArray()
        elements
        return false
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val typeName = element.parentOfType<ProtobufTypeName>()
    }

    override fun showHint(editor: Editor): Boolean {
        return false
    }
}
