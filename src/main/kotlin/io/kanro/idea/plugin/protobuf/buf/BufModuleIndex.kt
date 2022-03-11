package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
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

class BufModuleIndex : ScalarIndexExtension<String>() {
    override fun getName(): ID<String, Void> {
        return NAME
    }

    override fun getIndexer(): DataIndexer<String, Void, FileContent> {
        return DataIndexer {
            val file = it.psiFile as? YAMLFile ?: return@DataIndexer mapOf()
            val mapping = file.documents.firstOrNull()?.topLevelValue as? YAMLMapping ?: return@DataIndexer mapOf()
            val moduleName =
                mapping.keyValues.firstOrNull { it.keyText == "name" }?.valueText?.takeIf { it.isNotEmpty() }
                    ?: it.file.parent.path
            mapOf(moduleName to null)
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
        val NAME = ID.create<String, Void>("buf.module.name")

        private fun getAllModules(project: Project): List<String> {
            return FileBasedIndex.getInstance().getAllKeys(NAME, project).toList()
        }

        fun getModuleName(project: Project, yaml: VirtualFile): String? {
            return FileBasedIndex.getInstance().getFileData(NAME, yaml, project).keys.firstOrNull()
        }

        fun getModuleBufYaml(project: Project, psiElement: PsiElement): VirtualFile? {
            val file = psiElement.containingFile.originalFile
            return CachedValuesManager.getCachedValue(file) {
                val fileUrl = file.virtualFile.url
                var target: VirtualFile? = null

                getAllModules(project).forEach {
                    FileBasedIndex.getInstance()
                        .processValues(
                            NAME, it, null,
                            FileBasedIndex.ValueProcessor { file, _ ->
                                if (fileUrl.startsWith(file.parent.url)) {
                                    target = file
                                    false
                                } else {
                                    true
                                }
                            },
                            GlobalSearchScope.projectScope(project)
                        )
                }

                target ?: return@getCachedValue CachedValueProvider.Result.create(null)
                CachedValueProvider.Result.create(target, target)
            }
        }
    }
}
