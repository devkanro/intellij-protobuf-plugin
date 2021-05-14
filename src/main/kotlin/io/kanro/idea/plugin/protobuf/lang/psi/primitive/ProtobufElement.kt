package io.kanro.idea.plugin.protobuf.lang.psi.primitive

import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import io.kanro.idea.plugin.protobuf.lang.file.FileResolver
import io.kanro.idea.plugin.protobuf.lang.psi.ProtobufFile

interface ProtobufElement : PsiElement {
    @JvmDefault
    fun file(): ProtobufFile {
        return containingFile.originalFile as ProtobufFile
    }

    @JvmDefault
    fun importPath(context: ProtobufFile? = null): String? {
        val file = file()
        val module = ModuleUtil.findModuleForFile(context ?: file)
        return if (module != null) {
            FileResolver.getImportPath(file.virtualFile, module)
        } else {
            FileResolver.getImportPath(file.virtualFile, this.project)
        }
    }
}
