package com.github.akiym.intellijcopylines.actions

import com.github.akiym.intellijcopylines.utils.getRelativePath
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.StringSelection

class CopyFilenameAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val project = e.project ?: return
        val file = getRelativePath(project, psiFile.virtualFile)

        CopyPasteManager.getInstance().setContents(StringSelection(file))
    }
}
