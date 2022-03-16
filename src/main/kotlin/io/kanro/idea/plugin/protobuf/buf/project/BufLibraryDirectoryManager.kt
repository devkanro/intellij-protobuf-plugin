package io.kanro.idea.plugin.protobuf.buf.project

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection
import io.kanro.idea.plugin.protobuf.buf.util.BufFiles

@State(name = "BufLibraryDirectoryManager", storages = [Storage("protobuf.xml")])
class BufLibraryDirectoryManager(val project: Project) : PersistentStateComponent<BufYamlFileManager.State> {
    private val libraryRoots = mutableSetOf<VirtualFile>()

    private val libraryInfo = mutableMapOf<VirtualFile, LibraryInfo>()

    override fun getState(): BufYamlFileManager.State? {
        YAMLMapper()
        TODO("Not yet implemented")
    }

    override fun loadState(state: BufYamlFileManager.State) {
        TODO("Not yet implemented")
    }

    private fun addModuleInBackground(moduleRoot: Collection<VirtualFile>) {

    }

    private fun onAddBufModule(yaml: VirtualFile) {
    }

    class State() {
        @XCollection(propertyElementName = "libraries")
        val libraries: MutableList<Library> = mutableListOf()

        constructor(paths: List<Library>) : this() {
            this.libraries.addAll(paths)
        }
    }

    @Tag("library")
    data class Library(
        @Attribute("name")
        val name: String,
        @Attribute("path")
        val path: String,
        @Attribute("yamlPath")
        val yamlPath: String
    )

    data class LibraryInfo(
        val yamlFile: String,
        val name: String,
        val dependencies: List<String>
    )
}
