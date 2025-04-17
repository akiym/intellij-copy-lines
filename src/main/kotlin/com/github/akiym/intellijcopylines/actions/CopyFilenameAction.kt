package com.github.akiym.intellijcopylines.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.StringSelection
import kotlin.io.path.Path
import kotlin.io.path.pathString

class CopyFilenameAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val basePath = e.project?.basePath
        val file = if (basePath != null) {
            Path(basePath).relativize(psiFile.virtualFile.toNioPath()).pathString
        } else {
            psiFile.virtualFile.path
        }

        CopyPasteManager.getInstance().setContents(StringSelection(file))
    }
}
