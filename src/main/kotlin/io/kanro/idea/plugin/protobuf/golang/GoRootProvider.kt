package io.kanro.idea.plugin.protobuf.golang

import com.goide.project.GoRootsProvider
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRoot
import io.kanro.idea.plugin.protobuf.lang.root.ProtobufRootProvider
import io.kanro.idea.plugin.protobuf.lang.util.module

class GoRootProvider : ProtobufRootProvider {
    override fun id(): String? {
        return "goPath"
    }

    override fun roots(context: PsiElement): List<ProtobufRoot> {
        val roots = context.module?.let { module ->
            GoRootsProvider.EP_NAME.extensionList.flatMap {
                it.getGoPathSourcesRoots(module.project, module)
            }
        } ?: GoRootsProvider.EP_NAME.extensionList.flatMap { it.getGoPathSourcesRoots(context.project, null) }
        return roots.map {
            ProtobufRoot(null, it)
        }
    }
}
