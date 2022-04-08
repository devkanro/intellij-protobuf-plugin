package io.kanro.idea.plugin.protobuf.buf.settings

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import java.nio.file.Path

@State(
    name = "BufSettings",
    storages = [Storage("protobuf.xml")]
)
class BufSettings : SimplePersistentStateComponent<BufSettings.State>(State()) {
    fun bufPath(): Path? {
        return if (state.path.isNullOrEmpty()) {
            findBufCommand()
        } else {
            Path.of(state.path ?: return null)
        }
    }

    private fun findBufCommand(): Path? {
        return null
    }

    class State : BaseState() {
        var path by string()
    }
}
