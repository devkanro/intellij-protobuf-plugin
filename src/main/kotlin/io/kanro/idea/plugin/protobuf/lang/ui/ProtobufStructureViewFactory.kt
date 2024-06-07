package io.kanro.idea.plugin.protobuf.lang.ui

import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufFile
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufDefinition
import io.kanro.idea.plugin.protobuf.lang.psi.proto.structure.ProtobufScopeItemContainer

class ProtobufStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        if (psiFile !is ProtobufFile) return null
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return ProtobufStructureViewModel(psiFile, editor)
            }
        }
    }
}

class ProtobufStructureViewModel(file: PsiFile, editor: Editor?) :
    StructureViewModelBase(file, editor, ProtobufStructureViewElement(file)),
    StructureViewModel.ElementInfoProvider,
    StructureViewModel.ExpandInfoProvider {
    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean {
        return element?.value is ProtobufFile
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement?): Boolean {
        return false
    }

    override fun isAutoExpand(element: StructureViewTreeElement): Boolean {
        return element.value is ProtobufFile
    }

    override fun isSmartExpand(): Boolean {
        return false
    }
}

class ProtobufStructureViewElement(private val element: NavigatablePsiElement) :
    StructureViewTreeElement,
    SortableTreeElement {
    override fun getPresentation(): ItemPresentation {
        return element.presentation ?: TODO()
    }

    override fun getChildren(): Array<TreeElement> {
        if (element !is ProtobufScopeItemContainer) return arrayOf()
        return element.items().mapNotNull {
            if (it is ProtobufDefinition) ProtobufStructureViewElement(it) else null
        }.toTypedArray()
    }

    override fun navigate(requestFocus: Boolean) {
        element.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean {
        return element.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return element.canNavigateToSource()
    }

    override fun getValue(): Any {
        return element
    }

    override fun getAlphaSortKey(): String {
        return element.name ?: ""
    }
}
