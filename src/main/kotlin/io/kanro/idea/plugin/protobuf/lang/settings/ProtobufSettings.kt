package io.kanro.idea.plugin.protobuf.lang.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.util.ModificationTracker

@State(
    name = "ProtobufSettings",
    storages = [Storage("protobuf.xml")]
)
class ProtobufSettings : PersistentStateComponent<ProtobufSettings.State>, ModificationTracker {
    private var currentState = State()

    private var modificationCount = 0L

    override fun getState(): State {
        return currentState
    }

    override fun loadState(state: State) {
        if (state != currentState) {
            currentState = state
            modificationCount++
        }
    }

    data class ImportRootEntry(var path: String = "", var common: Boolean = true)

    data class State(
        var importRoots: Array<ImportRootEntry> = arrayOf()
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (!importRoots.contentEquals(other.importRoots)) return false

            return true
        }

        override fun hashCode(): Int {
            return importRoots.contentHashCode()
        }
    }

    override fun getModificationCount(): Long {
        return modificationCount
    }
}
