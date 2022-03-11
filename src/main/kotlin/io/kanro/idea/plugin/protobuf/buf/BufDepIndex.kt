package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import io.kanro.idea.plugin.protobuf.buf.util.isBufYaml
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLScalar
import org.jetbrains.yaml.psi.YAMLSequence

class BufDepIndex : ScalarIndexExtension<String>() {
    override fun getName(): ID<String, Void> {
        return NAME
    }

    override fun getIndexer(): DataIndexer<String, Void, FileContent> {
        return DataIndexer {
            val file = it.psiFile as? YAMLFile ?: return@DataIndexer mapOf()
            val mapping = file.documents.firstOrNull()?.topLevelValue as? YAMLMapping ?: return@DataIndexer mapOf()
            val depsField =
                mapping.keyValues.firstOrNull { it.keyText == "deps" }?.value as? YAMLSequence
                    ?: return@DataIndexer mapOf()
            depsField.items.asSequence()
                .mapNotNull {
                    (it.value as? YAMLScalar)?.textValue?.takeIf { it.isNotEmpty() }
                }.associateWith { null }
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getVersion(): Int {
        return 1
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return FileBasedIndex.InputFilter {
            isBufYaml(it.name)
        }
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    companion object {
        val NAME = ID.create<String, Void>("buf.module.deps.name")

        fun getModuleDepRoots(project: Project, buf: VirtualFile): List<VirtualFile> {
            val bufFile = PsiManager.getInstance(project).findFile(buf) ?: return listOf()

            return CachedValuesManager.getCachedValue(bufFile) {
                val result = mutableSetOf<VirtualFile>()
                val remoteDepYml = BufLockIndex.getModuleDepModules(project, buf)
                result += remoteDepYml.mapNotNull { it.parent }
                remoteDepYml.forEach {
                    result += getModuleDepRoots(project, it)
                }
                return@getCachedValue CachedValueProvider.Result.create(
                    result.toList(),
                    PsiModificationTracker.MODIFICATION_COUNT
                )
            }
        }
    }
}
