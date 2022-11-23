package com.github.akiym.intellijcopylines.actions

import com.github.akiym.intellijcopylines.settings.ApplicationSettingsState
import com.github.akiym.intellijcopylines.utils.SimpleStrSubstitutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.util.TextRange
import java.awt.datatransfer.StringSelection
import kotlin.io.path.Path
import kotlin.io.path.pathString

class CopyLinesAction : AnAction() {
    private val state
        get() = ApplicationSettingsState.instance.state

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)

        if (editor.caretModel.caretsAndSelections.size > 1) {
            return
        }

        val document = editor.document
        val sm = editor.selectionModel
        if (!sm.hasSelection()) {
            sm.setSelection(0, document.textLength)
        }
        val startLine = document.getLineNumber(sm.selectionStart)
        val endLine = document.getLineNumber(sm.selectionEnd).let {
            // Exclude last newline
            if (sm.selectionEnd == document.getLineStartOffset(it)) {
                it - 1
            } else {
                it
            }
        }
        val text = document.getText(
            TextRange(
                document.getLineStartOffset(startLine),
                document.getLineEndOffset(endLine),
            )
        )

        val psiFile = e.getRequiredData(CommonDataKeys.PSI_FILE)
        val basePath = e.project?.basePath
        val file = if (basePath != null) {
            Path(basePath).relativize(psiFile.virtualFile.toNioPath()).pathString
        } else {
            psiFile.virtualFile.path
        }

        val model = Model(
            startLine = startLine + 1,
            endLine = endLine + 1,
            file = file,
            extension = psiFile.fileType.defaultExtension,
            text = text,
        )

        val copy = SimpleStrSubstitutor(
            mapOf(
                "FILE" to model.file,
                "LINE" to model.line(state.lineFormat),
                "EXTENSION" to model.extension,
                "TEXT" to model.text,
            ),
        ).replace(state.format)

        CopyPasteManager.getInstance().setContents(StringSelection(copy))
        sm.removeSelection()
    }

    data class Model(
        val startLine: Int,
        val endLine: Int,
        val file: String,
        val extension: String,
        val text: String,
    ) {
        fun line(lineFormat: String) = if (startLine == endLine) {
            startLine.toString()
        } else {
            SimpleStrSubstitutor(
                mapOf(
                    "START" to startLine,
                    "END" to endLine,
                ),
            ).replace(lineFormat)
        }
    }
}
