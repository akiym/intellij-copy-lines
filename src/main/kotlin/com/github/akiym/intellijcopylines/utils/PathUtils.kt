package com.github.akiym.intellijcopylines.utils

import com.github.akiym.intellijcopylines.settings.ProjectSettingsState
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import kotlin.io.path.Path
import kotlin.io.path.pathString

fun getRelativePath(project: Project, virtualFile: VirtualFile): String {
    val projectBasePath = project.basePath ?: return virtualFile.path

    val relativePath = Path(projectBasePath).relativize(virtualFile.toNioPath()).pathString
    val configuredBasePath = ProjectSettingsState.getInstance(project).state.basePath
    if (configuredBasePath.isEmpty()) {
        return relativePath
    }

    val relPath = Path(relativePath)
    val basePath = Path(configuredBasePath).normalize()
    return if (relPath.startsWith(basePath)) {
        basePath.relativize(relPath).pathString
    } else {
        relativePath
    }
}
