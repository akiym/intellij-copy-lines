package com.github.akiym.intellijcopylines.utils

import com.github.akiym.intellijcopylines.settings.ProjectSettingsState
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.io.path.Path
import kotlin.io.path.pathString

fun getRelativePath(project: Project, virtualFile: VirtualFile): String {
    val relativePath = resolveRelativePath(project, virtualFile) ?: return virtualFile.name

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

private fun resolveRelativePath(project: Project, virtualFile: VirtualFile): String? {
    if (virtualFile.fileSystem !is LocalFileSystem) {
        return resolveRelativePathForRemote(project, virtualFile)
    }

    val projectBaseDir = project.guessProjectDir() ?: return null
    return VfsUtilCore.getRelativePath(virtualFile, projectBaseDir)
}

// Cache discovered project roots to avoid repeated synchronous remote calls via ThinClientVFS.findFileByPath
private val cachedRemoteProjectRoots = CopyOnWriteArraySet<String>()

private fun resolveRelativePathForRemote(project: Project, virtualFile: VirtualFile): String? {
    val filePath = virtualFile.path

    for (root in cachedRemoteProjectRoots) {
        if (filePath.startsWith("$root/")) {
            return filePath.removePrefix("$root/")
        }
    }

    // In ThinClientVFS, VirtualFile.parent is null, so walk up the path segments
    // and locate the project root by checking for the .idea directory at each level.
    // findFileByPath may timeout due to synchronous remote calls, so catch exceptions
    // and fall back to project name matching.
    try {
        val fs = virtualFile.fileSystem
        val segments = filePath.split("/")
        var candidateRoot = ""
        for (segment in segments.dropLast(1)) {
            if (segment.isEmpty()) continue
            candidateRoot += "/$segment"
            val ideaDir = fs.findFileByPath("$candidateRoot/.idea")
            if (ideaDir != null) {
                cachedRemoteProjectRoots.add(candidateRoot)
                return filePath.removePrefix("$candidateRoot/")
            }
        }
    } catch (_: Exception) {
        // findFileByPath may throw TimeoutException in ThinClientVFS
    }

    return resolveRelativePathByProjectName(project, filePath)
}

private fun resolveRelativePathByProjectName(project: Project, filePath: String): String? {
    val projectName = project.name
    val segments = filePath.split("/")
    val nameIdx = segments.indexOf(projectName)
    if (nameIdx >= 0) {
        val root = segments.subList(0, nameIdx + 1).joinToString("/")
        cachedRemoteProjectRoots.add(root)
        return segments.subList(nameIdx + 1, segments.size).joinToString("/")
    }
    return null
}
