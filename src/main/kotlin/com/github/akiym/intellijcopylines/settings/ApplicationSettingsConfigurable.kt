package com.github.akiym.intellijcopylines.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign

@Suppress("UnstableApiUsage")
class ApplicationSettingsConfigurable : Configurable {
    private val state
        get() = ApplicationSettingsState.instance.state

    private val panel: DialogPanel
    private val model = ApplicationSettingsState.Model()

    init {
        panel = panel {
            row {
                label("Format:")
                    .verticalAlign(VerticalAlign.TOP)
                    .gap(RightGap.SMALL)
                textArea()
                    .bindText(model::format)
                    .rows(5)
                    .horizontalAlign(HorizontalAlign.FILL)
            }.layout(RowLayout.PARENT_GRID).rowComment("""
                |${'$'}{FILE}: Relative file path from your project
                |${'$'}{LINE}: Selected line numbers (see below)
                |${'$'}{EXTENSION}: File extension
                |${'$'}{TEXT}: Selected text
            """.trimMargin().lines().joinToString("<br>"))

            row {
                label("${'$'}{LINE} format:")
                    .verticalAlign(VerticalAlign.TOP)
                    .gap(RightGap.SMALL)
                textArea()
                    .bindText(model::lineFormat)
                    .rows(5)
                    .horizontalAlign(HorizontalAlign.FILL)
            }.layout(RowLayout.PARENT_GRID).rowComment("""
                |${'$'}{START}: Starting line number
                |${'$'}{END}: Ending line number
                |If START and END are the same, this is ignored and only START is used.
            """.trimMargin().lines().joinToString("<br>"))
            row {
                text("")
            }
        }
    }

    override fun createComponent() = panel

    override fun isModified(): Boolean {
        panel.apply()

        return model != state
    }

    override fun apply() {
        panel.apply()

        with(state) {
            format = model.format
            lineFormat = model.lineFormat
        }
    }

    override fun reset() {
        with(model) {
            format = state.format
            lineFormat = state.lineFormat
        }

        panel.reset()
    }

    override fun getDisplayName() = "Copy Lines"
}
