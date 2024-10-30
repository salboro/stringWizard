package com.string.wizard.stringwizard.data.repository

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.ResourceString
import org.jetbrains.kotlin.idea.base.projectStructure.externalProjectPath
import java.io.File

private const val RES_DIRECTORY_PATH = "/src/main/res/"

class StringRepository {

    fun getStringResList(module: Module): List<ResourceString> {
        val path = module.externalProjectPath ?: error("Invalid module path: ${module.externalProjectPath}")
        val resDirectoryPath = path + RES_DIRECTORY_PATH
        val directory = File(resDirectoryPath).walk().filter { it.isDirectory && it.name == "values" }.first()

        return getStrings(directory.absolutePath)
    }

    private fun getStrings(directoryPath: String): List<ResourceString> {
        val resFile = File(directoryPath).walk().find { it.isFile && it.name == "strings.xml" } ?: return emptyList()
        val resText = resFile.readText()

        val rawStrings = resText
                .substringAfter("<resources>")
                .substringBefore("</resources>")
                .split("\n")
                .filter { it.isNotBlank() }

        return rawStrings.map {
            ResourceString(name = getResourcesStringName(it), value = getResourcesStringValue(it), directoryPath = directoryPath)
        }
    }

    private fun getResourcesStringName(from: String): String =
            from
                    .substringAfter("<string name=\"")
                    .substringBefore("\n")

    private fun getResourcesStringValue(from: String): String =
            from
                    .substringAfter("\">")
                    .substringBefore("</string>")
}