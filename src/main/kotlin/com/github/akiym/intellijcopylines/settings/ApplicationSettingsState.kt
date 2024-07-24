package com.github.akiym.intellijcopylines.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "ApplicationSettingsState",
    storages = [Storage("copy-lines.xml")]
)
class ApplicationSettingsState : PersistentStateComponent<ApplicationSettingsState.Model> {
    companion object {
        val instance: ApplicationSettingsState
            get() = ApplicationManager.getApplication().getService(ApplicationSettingsState::class.java)
    }

    private var state = Model()

    override fun getState(): Model {
        return state
    }

    override fun loadState(s: Model) {
        state = s
    }

    data class Model(
        var format: String = """
            |`${'$'}{FILE}` ${'$'}{LINE}行目
            |```${'$'}{EXTENSION}
            |${'$'}{TEXT}
            |```
        """.trimMargin(),
        var lineFormat: String = "${'$'}{START}~${'$'}{END}",
    )
}
