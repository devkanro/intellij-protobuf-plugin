package io.kanro.idea.plugin.protobuf.buf.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "BufSettings",
    storages = [Storage("protobuf.xml")]
)
class BufSettings : PersistentStateComponent<BufSettings.State> {
    private var currentState = State()

    override fun getState(): State {
        return currentState
    }

    override fun loadState(state: State) {
        currentState = state
    }

    data class State(
        var path: String = ""
    )
}
