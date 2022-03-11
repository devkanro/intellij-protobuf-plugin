package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.util.SimpleModificationTracker
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.ProjectScope
import com.intellij.ui.AppUIUtil
import com.intellij.util.concurrency.NonUrgentExecutor
import com.intellij.util.messages.Topic
import com.intellij.util.xmlb.annotations.XCollection
import io.kanro.idea.plugin.protobuf.buf.util.BUF_YAML
import io.kanro.idea.plugin.protobuf.buf.util.BUF_YML
import io.kanro.idea.plugin.protobuf.buf.util.isBufYaml
import io.kanro.idea.plugin.protobuf.lang.util.contentEquals

@State(name = "BufYamlFileManager", storages = [Storage("protobuf.xml")])
class BufYamlFileManager(val project: Project) : PersistentStateComponent<BufYamlFileManager.State> {
    private val tracker = SimpleModificationTracker()
    private val yamlFiles = mutableSetOf<VirtualFile>()

    override fun getState(): State? {
        return State(
            yamlFiles.asSequence().map { it.path }.sorted().toList()
        )
    }

    override fun loadState(state: State) {
        setBufYamlFiles(state.paths.mapNotNull { LocalFileSystem.getInstance().findFileByPath(it) })
    }

    fun modificationTracker(): ModificationTracker {
        return tracker
    }

    fun yamlFiles(): Collection<VirtualFile> {
        return yamlFiles.toList()
    }

    fun onAdded(file: VirtualFile, updateLibrary: Boolean) {
        synchronized(yamlFiles) {
            if (file in this.yamlFiles || !validBufYaml(file)) {
                return
            }

            tracker.incModificationCount()
            yamlFiles += file
        }

        if (updateLibrary) {
            updateLibrary()
        }
        publishEvent(BufYamlChangeEventType.CREATED, file)
    }

    fun onRemoved(file: VirtualFile) {
        synchronized(yamlFiles) {
            if (file !in this.yamlFiles) {
                return
            }

            tracker.incModificationCount()
            yamlFiles -= file
        }

        publishEvent(BufYamlChangeEventType.DELETED, file)
    }

    fun onChanged(file: VirtualFile) {
        synchronized(yamlFiles) {
            if (file !in this.yamlFiles) {
                return
            }

            tracker.incModificationCount()
        }

        publishEvent(BufYamlChangeEventType.UPDATED, file)
    }

    fun updateLibrary() {
    }

    fun publishEvent(type: BufYamlChangeEventType, file: VirtualFile) {
        AppUIUtil.invokeLaterIfProjectAlive(project) {
            if (file.isValid) {
                project.messageBus.syncPublisher(TOPIC).onChange(type, file)
            }
        }
    }

    fun rootChanged() {
        val old = synchronized(yamlFiles) {
            yamlFiles.toList()
        }

        ReadAction.run<RuntimeException> {
            if (!project.isDisposed) {
                val new = old.filter { validBufYaml(it) }
                if (!new.contentEquals(old)) {
                    tracker.incModificationCount()
                    DaemonCodeAnalyzer.getInstance(project).restart()
                }
            }
        }
    }

    private fun validBufYaml(file: VirtualFile): Boolean {
        return file.isValid && isBufYaml(file.name) && !file.isDirectory && ProjectFileIndex.getInstance(project)
            .getContentRootForFile(file) != null
    }

    private fun setBufYamlFiles(files: Collection<VirtualFile>) {
        val validFiles = files.filter { validBufYaml(it) }

        synchronized(yamlFiles) {
            yamlFiles.clear()
            yamlFiles += validFiles
        }

        tracker.incModificationCount()
    }

    fun detectAllBufYamlFiles() {
        ReadAction.nonBlocking {
            if (!this.project.isDisposed) {
                val yamlFiles =
                    FilenameIndex.getVirtualFilesByName(BUF_YAML, ProjectScope.getContentScope(project))
                val ymlFiles =
                    FilenameIndex.getVirtualFilesByName(BUF_YML, ProjectScope.getContentScope(project))

                setBufYamlFiles(sequenceOf(yamlFiles, ymlFiles).flatten().toList())
            }
        }.inSmartMode(project).submit(NonUrgentExecutor.getInstance())
    }

    fun interface BufYamlChangeListener {
        fun onChange(type: BufYamlChangeEventType, file: VirtualFile)
    }

    enum class BufYamlChangeEventType {
        CREATED, UPDATED, DELETED
    }

    class State() {
        @XCollection(propertyElementName = "bufYamlPaths", elementName = "path")
        val paths: MutableList<String> = mutableListOf()

        constructor(paths: List<String>) : this() {
            this.paths.addAll(paths)
        }
    }

    class StartActivity : StartupActivity.DumbAware {
        override fun runActivity(project: Project) {
            project.getService(BufYamlFileManager::class.java).detectAllBufYamlFiles()
        }
    }

    companion object {
        val TOPIC = Topic(BufYamlChangeListener::class.java, Topic.BroadcastDirection.NONE)
    }
}
