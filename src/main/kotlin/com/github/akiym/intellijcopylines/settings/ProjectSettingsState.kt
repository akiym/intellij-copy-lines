package com.github.akiym.intellijcopylines.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(
    name = "CopyLinesProjectSettingsState",
    storages = [Storage("copy-lines-project.xml")]
)
class ProjectSettingsState : PersistentStateComponent<ProjectSettingsState.Model> {
    companion object {
        fun getInstance(project: Project): ProjectSettingsState =
            project.getService(ProjectSettingsState::class.java)
    }

    private var state = Model()

    override fun getState(): Model {
        return state
    }

    override fun loadState(s: Model) {
        state = s
    }

    data class Model(
        var basePath: String = ""
    )
}
