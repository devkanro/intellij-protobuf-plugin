package io.kanro.idea.plugin.protobuf.buf.settings

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import io.kanro.idea.plugin.protobuf.buf.util.isWindows
import java.nio.file.Path

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

    fun bufPath(): Path? {
        return if (state.path.isEmpty()) {
            findBufCommand()
        } else {
            Path.of(state.path)
        }
    }

    private fun findBufCommand(): Path? {
        return null
        val command = if (isWindows()) {
            GeneralCommandLine("where", "buf")
        } else {
            GeneralCommandLine("command", "-v", "buf")
        }

        val output = ExecUtil.execAndGetOutput(command)
        if (output.exitCode != 0) return null
        return Path.of(output.stdout)
    }

    class State(
        var path: String = ""
    )
}
