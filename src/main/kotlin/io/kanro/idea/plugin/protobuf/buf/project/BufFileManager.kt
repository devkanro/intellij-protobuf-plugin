package io.kanro.idea.plugin.protobuf.buf.project

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.util.EmptyRunnable
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.pointers.VirtualFilePointer
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.ProjectScope
import com.intellij.util.concurrency.NonUrgentExecutor
import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufDepsFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLockDepsCommitSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLockDepsFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLockDepsOwnerSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLockDepsRemoteSchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufLockDepsRepositorySchema
import io.kanro.idea.plugin.protobuf.buf.schema.common.BufNameFieldSchema
import io.kanro.idea.plugin.protobuf.buf.schema.v1.BufWorkDirectoriesFieldSchema
import io.kanro.idea.plugin.protobuf.buf.util.BUF_LOCK
import io.kanro.idea.plugin.protobuf.buf.util.BUF_WORK_YAML
import io.kanro.idea.plugin.protobuf.buf.util.BUF_YAML
import io.kanro.idea.plugin.protobuf.buf.util.BufFiles
import java.util.Stack

@State(name = "BufFileManager", storages = [Storage("protobuf.xml")])
class BufFileManager(val project: Project) : PersistentStateComponent<BufFileManager.State> {
    private val logger = Logger.getInstance(BufFileListener::class.java)
    private val state = State()
    private val yamlMapper = YAMLMapper()
    private val cacheRootPointer = VirtualFilePointerManager.getInstance().createDirectoryPointer(
        VfsUtil.pathToUrl(BufFiles.getCacheRoot().toString()),
        true,
        project,
        object : VirtualFilePointerListener {
            override fun validityChanged(pointers: Array<out VirtualFilePointer>) {
                ReadAction.nonBlocking {
                    refreshLibraries()
                }.inSmartMode(project).submit(NonUrgentExecutor.getInstance())
            }
        }
    )

    init {
        importProject()
    }

    fun cacheRoot(): VirtualFile? {
        return cacheRootPointer.file
    }

    fun moduleChanged(oldRoot: VirtualFile?, newRoot: VirtualFile?) {
        val old = oldRoot?.let { root ->
            state.modules.firstOrNull { it.path == root.path }
        }
        val new = newRoot?.findChild(BUF_YAML)?.let {
            loadModule(it)
        }
        if (old == null) {
            new?.let {
                state.modules += it
                state.modules.sortBy { it.path }
            }
            return
        }

        if (new == null) {
            state.modules.remove(old)
        } else {
            old.copyFrom(new)
            state.modules.sortBy { it.path }
        }
    }

    fun workspaceChanged(oldRoot: VirtualFile?, newRoot: VirtualFile?) {
        val old = oldRoot?.let { root ->
            state.workspaces.firstOrNull { it.path == root.path }
        }
        val new = newRoot?.findChild(BUF_WORK_YAML)?.let {
            loadWorkspace(it)
        }
        if (old == null) {
            new?.let {
                state.workspaces += it
                state.workspaces.sortBy { it.path }
            }
            return
        }

        if (new == null) {
            state.workspaces.remove(old)
        } else {
            old.copyFrom(new)
            state.workspaces.sortBy { it.path }
        }
    }

    fun libraryChanged(oldRoot: VirtualFile?, newRoot: VirtualFile?) {
        val modificationCount = state.modificationCount
        val old = oldRoot?.let { root ->
            state.libraries.firstOrNull { it.path == root.path }
        }
        val new = newRoot?.findChild(BUF_YAML)?.let {
            val commit = it.parent.name
            val repo = it.parent.parent.name
            val owner = it.parent.parent.parent.name
            val remote = it.parent.parent.parent.parent.name
            loadModule(it, "$remote/$owner/$repo:$commit")
        }
        if (old == null) {
            new?.let {
                state.libraries += it
                state.libraries.sortBy { it.path }
            }
            return
        }

        if (new == null) {
            state.libraries.remove(old)
        } else {
            old.copyFrom(new)
            state.libraries.sortBy { it.path }
        }

        if (state.modificationCount != modificationCount) {
            notifyLibraryRootsChanged()
        }
    }

    private fun importProject() {
        ReadAction.nonBlocking {
            if (this.project.isDisposed) return@nonBlocking
            state.modules.clear()
            state.workspaces.clear()

            val yamlFiles =
                FilenameIndex.getVirtualFilesByName(BUF_YAML, ProjectScope.getContentScope(project))
            val workYamlFiles =
                FilenameIndex.getVirtualFilesByName(BUF_WORK_YAML, ProjectScope.getContentScope(project))
            val modules = (yamlFiles).mapNotNull {
                loadModule(it)
            }.distinctBy { it.path }
            val workspaces = (workYamlFiles).mapNotNull {
                loadWorkspace(it)
            }.distinctBy { it.path }

            state.modules += modules.sortedBy { it.path }
            state.workspaces += workspaces.sortedBy { it.path }
            refreshLibraries()
        }.inSmartMode(project).submit(NonUrgentExecutor.getInstance())
    }

    private fun readYaml(yaml: VirtualFile?): JsonNode? {
        return ApplicationManager.getApplication().runReadAction<JsonNode?> {
            yaml?.inputStream?.use {
                try {
                    yamlMapper.readTree(it)
                } catch (e: JacksonException) {
                    null
                }
            }
        }
    }

    private fun loadModule(yaml: VirtualFile, reference: String? = null): State.Module? {
        val content = readYaml(yaml) ?: return null
        val module = State.Module()
        module.reference = reference
        module.name = content.get(BufNameFieldSchema.name)?.textValue()
        module.path = yaml.parent.path
        module.yaml = yaml.path
        module.dependencies += (content.get(BufDepsFieldSchema.name)?.mapNotNull { it.textValue() } ?: listOf())
        readYaml(yaml.parent.findChild(BUF_LOCK))?.let {
            it.get(BufLockDepsFieldSchema.name)?.mapNotNull {
                val remote = it.get(BufLockDepsRemoteSchema.name)?.textValue() ?: return@mapNotNull null
                val owner = it.get(BufLockDepsOwnerSchema.name)?.textValue() ?: return@mapNotNull null
                val repository = it.get(BufLockDepsRepositorySchema.name)?.textValue() ?: return@mapNotNull null
                val commit = it.get(BufLockDepsCommitSchema.name)?.textValue() ?: return@mapNotNull null
                State.Dependency().apply {
                    this.remote = remote
                    this.owner = owner
                    this.repository = repository
                    this.commit = commit
                }
            }
        }?.let {
            module.lockedDependencies += it
        }
        return module
    }

    private fun loadWorkspace(yaml: VirtualFile): State.Workspace? {
        val root = yaml.parent ?: return null
        val content = readYaml(yaml) ?: return null
        val moduleRoots = content.get(BufWorkDirectoriesFieldSchema.name)?.mapNotNull {
            root.findFileByRelativePath(it.textValue())?.path
        } ?: listOf()

        return State.Workspace().apply {
            this.path = yaml.parent.path
            this.yaml = yaml.path
            this.roots += moduleRoots
        }
    }

    fun refreshLibraries() {
        state.libraries.clear()
        val root = VirtualFileManager.getInstance().findFileByNioPath(BufFiles.getCacheRoot()) ?: return
        val libraries = mutableListOf<State.Module>()
        root.children.forEach {
            val remote = it.name
            it.children.forEach {
                val owner = it.name
                it.children.forEach {
                    val repo = it.name
                    it.children.forEach {
                        val commit = it.name
                        val yaml = it.findChild(BUF_YAML) ?: return@forEach
                        loadModule(yaml, "$remote/$owner/$repo:$commit")?.let {
                            libraries += it
                        }
                    }
                }
            }
        }
        state.libraries += libraries.sortedBy { it.path }
        notifyLibraryRootsChanged()
    }

    private fun notifyLibraryRootsChanged() {
        ApplicationManager.getApplication().invokeLater {
            ApplicationManager.getApplication().runWriteAction {
                ProjectRootManagerEx.getInstanceEx(project).makeRootsChange(EmptyRunnable.getInstance(), false, true)
            }
        }
    }

    override fun getState(): State {
        return state
    }

    override fun loadState(state: State) {
        val modificationCount = state.modificationCount
        this.state.copyFrom(state)
        if (state.modificationCount != modificationCount) {
            notifyLibraryRootsChanged()
        }
    }

    fun findModule(file: VirtualFile): State.Module? {
        return state.modules.firstOrNull {
            VfsUtil.isUnder(file.path, listOfNotNull(it.path))
        }
    }

    fun findLibrary(file: VirtualFile): State.Module? {
        return state.libraries.firstOrNull {
            VfsUtil.isUnder(file.path, listOfNotNull(it.path))
        }
    }

    fun findModuleOrLibrary(file: VirtualFile): State.Module? {
        return findModule(file) ?: findLibrary(file)
    }

    fun findModuleFromPsiElement(element: PsiElement): State.Module? {
        val file = element.containingFile.originalFile.virtualFile ?: return null
        return findModule(file)
    }

    fun findLibraryFromPsiElement(element: PsiElement): State.Module? {
        val file = element.containingFile.originalFile.virtualFile ?: return null
        return findLibrary(file)
    }

    fun findModuleOrLibraryFromPsiElement(element: PsiElement): State.Module? {
        val file = element.containingFile.originalFile.virtualFile ?: return null
        return findModule(file) ?: findLibrary(file)
    }

    fun findModulesInWorkspace(workspace: State.Workspace): List<State.Module> {
        return state.modules.filter {
            it.path in workspace.roots
        }
    }

    fun resolveDependencies(deps: Collection<State.Dependency>): List<State.Module> {
        val libraries = state.libraries.associateBy { it.reference }
        val required = Stack<State.Dependency>()
        val result = mutableListOf<State.Module>()
        val resolved = mutableSetOf<String>()

        required += deps
        while (required.isNotEmpty()) {
            val dep = required.pop()
            val name = dep.name()
            if (name in resolved) continue
            resolved += name
            val lib = libraries[name] ?: continue
            required += lib.lockedDependencies
            result += lib
        }

        return result
    }

    class State : BaseState() {
        @get:XCollection(propertyElementName = "modules", style = XCollection.Style.v2)
        val modules by list<Module>()

        @get:XCollection(propertyElementName = "workspaces", style = XCollection.Style.v2)
        val workspaces by list<Workspace>()

        @get:XCollection(propertyElementName = "libraries", style = XCollection.Style.v2)
        val libraries by list<Module>()

        @get:Tag
        var cache by string()

        @Tag("module")
        class Module : BaseState() {
            @get:Tag
            var reference by string(null)

            @get:Tag
            var name by string(null)

            @get:Tag
            var path by string(null)

            @get:Tag
            var yaml by string(null)

            @get:Tag
            var dependencies by stringSet()

            @get:XCollection(propertyElementName = "lockedDependencies", style = XCollection.Style.v2)
            val lockedDependencies by list<Dependency>()
        }

        @Tag("workspace")
        class Workspace : BaseState() {
            @get:Tag
            var path by string(null)

            @get:Tag
            var yaml by string(null)

            @get:XCollection(propertyElementName = "roots", elementName = "root", style = XCollection.Style.v2)
            var roots by stringSet()
        }

        @Tag("dependency")
        class Dependency : BaseState() {
            @get:Tag
            var remote by string(null)

            @get:Tag
            var owner by string(null)

            @get:Tag
            var repository by string(null)

            @get:Tag
            var commit by string(null)

            fun name(): String {
                return "${nameWithoutCommit()}:$commit"
            }

            fun nameWithoutCommit(): String {
                return "$remote/$owner/$repository"
            }

            override fun toString(): String {
                return name()
            }
        }
    }
}
