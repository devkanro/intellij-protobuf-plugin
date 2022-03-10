package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import io.kanro.idea.plugin.protobuf.buf.util.isBufWorkYaml
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLScalar
import org.jetbrains.yaml.psi.YAMLSequence

class BufWorkspaceIndex : ScalarIndexExtension<String>() {
    override fun getName(): ID<String, Void> {
        return NAME
    }

    override fun getIndexer(): DataIndexer<String, Void, FileContent> {
        return DataIndexer {
            val file = it.psiFile as? YAMLFile ?: return@DataIndexer mapOf()
            val mapping = file.documents.firstOrNull()?.topLevelValue as? YAMLMapping ?: return@DataIndexer mapOf()
            val directories =
                mapping.keyValues.firstOrNull { it.keyText == "directories" }?.value as? YAMLSequence
                    ?: return@DataIndexer mapOf()
            directories.items.mapNotNull {
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
            isBufWorkYaml(it.name)
        }
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    companion object {
        val NAME = ID.create<String, Void>("buf.workspace.directories")

        private fun getAllModules(project: Project): List<String> {
            return FileBasedIndex.getInstance().getAllKeys(NAME, project).toList()
        }

        fun getBufWorkspaceYaml(project: Project, buf: VirtualFile): VirtualFile? {
            val bufRoot = buf.parent ?: return null
            var target: VirtualFile? = null

            getAllModules(project).forEach {
                FileBasedIndex.getInstance().processValues(NAME, it, null, FileBasedIndex.ValueProcessor { file, _ ->
                    if (file.parent.findFileByRelativePath(it)?.url == bufRoot.url) {
                        target = file
                        false
                    } else {
                        true
                    }
                }, GlobalSearchScope.projectScope(project))
            }

            return target
        }

        fun getBufLocalDepRoots(project: Project, psiElement: PsiElement): List<VirtualFile> {
            val bufRoot = BufModuleIndex.getModuleBufYaml(project, psiElement)?.parent ?: return listOf()
            val target = mutableListOf<VirtualFile>()

            getAllModules(project).forEach {
                FileBasedIndex.getInstance().processValues(NAME, it, null, FileBasedIndex.ValueProcessor { file, _ ->
                    val workspaceRoot = file.parent
                    if (workspaceRoot.findFileByRelativePath(it)?.url == bufRoot.url) {
                        val directories = FileBasedIndex.getInstance().getFileData(NAME, file, project).keys
                        target += directories.mapNotNull { workspaceRoot.findFileByRelativePath(it) }
                        false
                    } else {
                        true
                    }
                }, GlobalSearchScope.projectScope(project))
            }

            if (target.isEmpty()) {
                target += bufRoot
            }

            return target
        }
    }
}