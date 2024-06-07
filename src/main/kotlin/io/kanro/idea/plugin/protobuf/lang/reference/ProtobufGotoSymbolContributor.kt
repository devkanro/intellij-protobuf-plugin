package io.kanro.idea.plugin.protobuf.lang.reference

import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.kanro.idea.plugin.protobuf.lang.psi.proto.ProtobufElement
import io.kanro.idea.plugin.protobuf.lang.psi.proto.stub.index.QualifiedNameIndex

class ProtobufGotoSymbolContributor : ChooseByNameContributor {
    override fun getNames(
        project: Project,
        includeNonProjectItems: Boolean,
    ): Array<String> {
        return StubIndex.getInstance().getAllKeys(
            QualifiedNameIndex.key,
            project,
        ).toTypedArray()
    }

    override fun getItemsByName(
        name: String,
        pattern: String?,
        project: Project,
        includeNonProjectItems: Boolean,
    ): Array<NavigationItem> {
        val scope =
            if (includeNonProjectItems) {
                GlobalSearchScope.allScope(project)
            } else {
                GlobalSearchScope.projectScope(project)
            }
        return StubIndex.getElements(
            QualifiedNameIndex.key,
            name,
            project,
            scope,
            ProtobufElement::class.java,
        ).filterIsInstance<NavigationItem>().toTypedArray()
    }
}
