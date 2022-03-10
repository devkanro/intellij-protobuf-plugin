package io.kanro.idea.plugin.protobuf.buf.project

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.Key
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.model.project.AbstractExternalEntityData
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.service.project.manage.AbstractProjectDataService
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalProjectImportBuilder
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalProjectImportProvider
import com.intellij.openapi.externalSystem.service.settings.AbstractImportFromExternalSystemControl
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemSettings
import com.intellij.openapi.externalSystem.settings.ExternalProjectSettings
import com.intellij.openapi.externalSystem.settings.ExternalSystemSettingsListener
import com.intellij.openapi.externalSystem.util.ExternalSystemSettingsControl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullFactory
import io.kanro.idea.plugin.protobuf.Icons
import java.io.File
import javax.swing.Icon

class BufProjectData : AbstractExternalEntityData(BufProjectImportProvider.ID) {
    companion object {
        val KEY = Key.create(
            BufProjectData::class.java, ProjectKeys.PROJECT.processingWeight + 1
        )
    }
}

class BufProjectDataService : AbstractProjectDataService<BufProjectData, Project?>() {
    override fun getTargetDataKey(): Key<BufProjectData> = BufProjectData.KEY


}

class BufProjectSettings : ExternalProjectSettings() {
    override fun clone(): ExternalProjectSettings {
        return BufProjectSettings()
    }
}

class BufSystemSettings :
    AbstractExternalSystemSettings<BufSystemSettings, BufProjectSettings, BufProjectSettingsListener>() {
    override fun subscribe(listener: ExternalSystemSettingsListener<BufProjectSettings>) {
        TODO("Not yet implemented")
    }

    override fun copyExtraSettingsFrom(settings: BufSystemSettings) {
        TODO("Not yet implemented")
    }

    override fun checkSettings(old: BufProjectSettings, current: BufProjectSettings) {
        TODO("Not yet implemented")
    }
}

class BufProjectSettingsListener : ExternalSystemSettingsListener<BufProjectSettings> {
    override fun onProjectRenamed(oldName: String, newName: String) {
        TODO("Not yet implemented")
    }

    override fun onProjectsLinked(settings: MutableCollection<BufProjectSettings>) {
        TODO("Not yet implemented")
    }

    override fun onProjectsUnlinked(linkedProjectPaths: MutableSet<String>) {
        TODO("Not yet implemented")
    }

    override fun onBulkChangeStart() {
        TODO("Not yet implemented")
    }

    override fun onBulkChangeEnd() {
        TODO("Not yet implemented")
    }
}

class BufProjectImportFromExternalSystemControl(
    projectSettings: BufProjectSettings,
    systemSettings: BufSystemSettings
) : AbstractImportFromExternalSystemControl<BufProjectSettings, BufProjectSettingsListener, BufSystemSettings>(
    BufProjectImportProvider.ID, systemSettings, projectSettings
) {
    override fun onLinkedProjectPathChange(path: String) {
        TODO("Not yet implemented")
    }

    override fun createProjectSettingsControl(settings: BufProjectSettings): ExternalSystemSettingsControl<BufProjectSettings> {
        TODO("Not yet implemented")
    }

    override fun createSystemSettingsControl(settings: BufSystemSettings): ExternalSystemSettingsControl<BufSystemSettings>? {
        TODO("Not yet implemented")
    }
}

class BufProjectControlFactory : NotNullFactory<BufProjectImportFromExternalSystemControl> {
    override fun create(): BufProjectImportFromExternalSystemControl {
        TODO("Not yet implemented")
    }

}

class BufProjectImportBuilder(
    projectDataManager: ProjectDataManager,
    control: BufProjectControlFactory,
) : AbstractExternalProjectImportBuilder<BufProjectImportFromExternalSystemControl>(
    projectDataManager,
    control,
    BufProjectImportProvider.ID
) {
    override fun getName(): String {
        return "Buf project"
    }

    override fun getIcon(): Icon {
        return Icons.BUF_LOGO
    }

    override fun doPrepare(context: WizardContext) {
        TODO("Not yet implemented")
    }

    override fun beforeCommit(dataNode: DataNode<ProjectData>, project: Project) {
        TODO("Not yet implemented")
    }

    override fun getExternalProjectConfigToUse(file: File): File {
        TODO("Not yet implemented")
    }

    override fun applyExtraSettings(context: WizardContext) {
        TODO("Not yet implemented")
    }
}

class BufProjectImportProvider : AbstractExternalProjectImportProvider(BufProjectImportBuilder(), ID) {
    companion object {
        val ID = ProjectSystemId("buf.build", "BUF PROJECT")
    }
}