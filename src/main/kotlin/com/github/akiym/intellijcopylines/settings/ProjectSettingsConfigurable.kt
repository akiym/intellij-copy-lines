package com.github.akiym.intellijcopylines.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

@Suppress("UnstableApiUsage")
class ProjectSettingsConfigurable(private val project: Project) : Configurable {
    private val state
        get() = ProjectSettingsState.getInstance(project).state

    private val panel: DialogPanel
    private val model = ProjectSettingsState.Model()

    init {
        panel = panel {
            row("Base path:") {
                textField()
                    .bindText(model::basePath)
                    .align(AlignX.FILL)
            }.rowComment("If set, paths will be relative to this base path instead of the project root.")
        }
    }

    override fun createComponent() = panel

    override fun isModified(): Boolean {
        panel.apply()

        return model != state
    }

    override fun apply() {
        panel.apply()

        state.basePath = model.basePath
    }

    override fun reset() {
        model.basePath = state.basePath

        panel.reset()
    }

    override fun getDisplayName() = "Project Settings"
}
