package io.kanro.idea.plugin.protobuf.lang.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "ProtobufSettings",
    storages = [Storage("protobuf.xml")]
)
class ProtobufSettings : PersistentStateComponent<ProtobufSettings.State> {
    private var currentState = State()

    override fun getState(): State {
        return currentState
    }

    override fun loadState(state: State) {
        currentState = state
    }

    data class ImportRootEntry(var path: String = "", var common: Boolean = true)

    data class State(
        var importRoots: List<ImportRootEntry> = listOf()
    )
}
