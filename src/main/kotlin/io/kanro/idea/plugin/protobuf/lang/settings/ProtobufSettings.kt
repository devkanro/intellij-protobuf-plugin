package io.kanro.idea.plugin.protobuf.lang.settings

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.util.ModificationTracker
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection

@State(
    name = "ProtobufSettings",
    storages = [Storage("protobuf.xml")]
)
class ProtobufSettings : SimplePersistentStateComponent<ProtobufSettings.State>(State()), ModificationTracker {
    override fun getModificationCount(): Long {
        return stateModificationCount
    }

    @Tag("entry")
    class ImportRootEntry(path: String? = null, common: Boolean = true) : BaseState() {
        @get:Attribute
        var path by string()

        @get:Attribute
        var common by property(true)

        init {
            this.path = path
            this.common = common
        }
    }

    class State : BaseState() {
        @get:XCollection(propertyElementName = "roots", style = XCollection.Style.v2)
        var importRoots by list<ImportRootEntry>()
    }
}
