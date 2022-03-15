package io.kanro.idea.plugin.protobuf.buf

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLockDepsCommitSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLockDepsOwnerSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLockDepsRemoteSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLockDepsRepositorySchema
import io.kanro.idea.plugin.protobuf.buf.util.BUF_LOCK
import io.kanro.idea.plugin.protobuf.buf.util.BUF_YAML
import io.kanro.idea.plugin.protobuf.buf.util.BUF_YML
import io.kanro.idea.plugin.protobuf.buf.util.isBufLock
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLSequence
import java.nio.file.Path

class BufLockIndex : ScalarIndexExtension<String>() {
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
                .mapNotNull { it.value }
                .filterIsInstance<YAMLMapping>()
                .mapNotNull {
                    val fields = it.keyValues.associateBy { it.keyText }
                    val remote = fields[BufLockDepsRemoteSchema.name]?.valueText ?: return@mapNotNull null
                    val owner = fields[BufLockDepsOwnerSchema.name]?.valueText ?: return@mapNotNull null
                    val repository = fields[BufLockDepsRepositorySchema.name]?.valueText ?: return@mapNotNull null
                    val commit = fields[BufLockDepsCommitSchema.name]?.valueText ?: return@mapNotNull null
                    "$remote/$owner/$repository/$commit"
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
            isBufLock(it.name)
        }
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    companion object {
        val NAME = ID.create<String, Void>("buf.module.lock.deps.name")

        fun getBufLock(project: Project, buf: VirtualFile): VirtualFile? {
            return buf.parent.findChild(BUF_LOCK)
        }

        fun getModuleDepModules(project: Project, lock: VirtualFile): List<VirtualFile> {
            val cacheRoot = VirtualFileManager.getInstance().findFileByNioPath(getCacheRoot()) ?: return listOf()
            return FileBasedIndex.getInstance().getFileData(NAME, lock, project).keys.mapNotNull {
                val root = cacheRoot.findFileByRelativePath(it)
                root?.findChild(BUF_YAML) ?: root?.findChild(BUF_YML)
            }
        }
    }
}
