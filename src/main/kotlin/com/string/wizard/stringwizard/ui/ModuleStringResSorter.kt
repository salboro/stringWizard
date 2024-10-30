package com.string.wizard.stringwizard.ui

import com.string.wizard.stringwizard.domain.usecase.GetResPathUseCase
import java.io.File

class ModuleStringResSorter(
    private val getResPathUseCase: GetResPathUseCase
) {

    private companion object {

        const val VALUES_DIRECTORY_NAME = "values"
        const val STRINGS_RES_FILE_STARTS = "strings"
        const val XML_FILE_FORMAT = ".xml"
        const val RESOURCES_TAG_START = "<resources>"
        const val RESOURCES_TAG_END = "</resources>"
        const val RESOURCES_FILE_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        const val SEPARATOR = "\n"
    }

    fun sort(modulePath: String) {
        val resPath = modulePath + getResPathUseCase()
        val directoriesWithStringsSequence = getValueDirectories(resPath)

        directoriesWithStringsSequence.forEach { stringsDirectory ->
            sortAllStrings(resPath, stringsDirectory.name)
        }
    }

    private fun getValueDirectories(moduleResPath: String): Sequence<File> =
        File(moduleResPath)
            .walk()
            .filter { it.isDirectory && it.name.startsWith(VALUES_DIRECTORY_NAME) }

    private fun sortAllStrings(resDirectoryPath: String, dirName: String) {
        val targetDirectoryPath = resDirectoryPath + dirName
        val targetFile = getTargetStringsFile(targetDirectoryPath) ?: throw Exception() // stay tuned
        val allStrings = targetFile
            .readText()
            .substringAfter(RESOURCES_TAG_START)
            .substringBefore(RESOURCES_TAG_END)

        val sortedStrings = allStrings
            .split(SEPARATOR)
            .filter { it.isNotBlank() }
            .map { it.trim() }
            .sorted()

        targetFile.writeText(generateNewResFileContent(sortedStrings))
    }

    private fun getTargetStringsFile(directoryPath: String): File? =
        File(directoryPath)
            .walk()
            .find {
                it.isFile
                        && it.name.startsWith(STRINGS_RES_FILE_STARTS)
                        && it.name.endsWith(XML_FILE_FORMAT)
            }

    private fun generateNewResFileContent(strings: List<String>): String =
        """
		|$RESOURCES_FILE_HEADER
		|$RESOURCES_TAG_START
		|${strings.joinToString(separator = SEPARATOR) { "\t$it" }}
		|$RESOURCES_TAG_END
	""".trimMargin()
}
