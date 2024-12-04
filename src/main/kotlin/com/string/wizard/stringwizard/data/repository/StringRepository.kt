package com.string.wizard.stringwizard.data.repository

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.util.XmlTemplate
import org.jetbrains.kotlin.idea.base.projectStructure.externalProjectPath
import java.io.File

class StringRepository {

	private companion object {

		const val RES_DIRECTORY_PATH = "/src/main/res/"
		const val STRINGS_FILE_NAME = "strings.xml"
	}

	fun getStringResList(module: Module): List<ResourceString> {
		val allDirectories = getAllValuesDirectories(module).ifEmpty { error("No strings in module ${module.name}") }
		val defaultDirectory = allDirectories.find { it.name == Locale.EN.packageName } ?: allDirectories.first()
		val stringsFile = getStringsFileFromDirectory(defaultDirectory)

		return getStrings(stringsFile, defaultDirectory.name)
	}

	private fun getStrings(stringsFile: File, directoryName: String): List<ResourceString> {
		val resText = stringsFile.readText()
		val locale = Locale.findByPackageName(directoryName) ?: throw IllegalArgumentException("Unexpected directory locale: ${stringsFile.absolutePath}")

		val rawStrings = resText
			.substringAfter("<resources>")
			.substringBefore("</resources>")
			.split("\n")
			.filter { it.isNotBlank() }

		return rawStrings.map {
			ResourceString(name = getResourcesStringName(it), value = getResourcesStringValue(it), locale = locale)
		}
	}

	private fun getResourcesStringName(from: String): String =
		from
			.substringAfter("<string name=\"")
			.substringBefore("\"")

	private fun getResourcesStringValue(from: String): String =
		from
			.substringAfter("\">")
			.substringBefore("</string>")

	fun copyString(sourceModule: Module, targetModule: Module, stringSourceName: String, stringTargetName: String) {
		val sourceStrings = getAllLocaleStrings(stringSourceName, sourceModule)
		val stringsWithNewName = sourceStrings.map { it.copy(name = stringTargetName) }

		writeNewStringsInAllLocale(targetModule, stringsWithNewName)
	}

	private fun getAllLocaleStrings(stringName: String, module: Module): List<ResourceString> {
		val valuesDirectories = getAllValuesDirectories(module)

		return valuesDirectories.map { directory ->
			val stringsFile = getStringsFileFromDirectory(directory)
			val locale = Locale.findByPackageName(directory.name) ?: throw IllegalArgumentException("Unexpected directory locale: ${directory.absolutePath}")
			ResourceString(name = stringName, value = getStringValue(stringsFile, stringName), locale = locale)
		}
	}

	private fun getStringValue(file: File, stringName: String): String {
		val stringPrefix = "<string name=\"$stringName\">"
		val fileText = file.readText()

		return if (fileText.contains(stringPrefix)) {
			fileText
				.substringAfter(stringPrefix)
				.substringBefore("</string>")
		} else {
			throw IllegalArgumentException("File ${file.path} does not have string $stringName")
		}
	}

	fun writeNewStringsInAllLocale(module: Module, strings: List<ResourceString>) {
		val directories = getAllValuesDirectories(module)

		directories.forEach { directory ->
			val stringsFile = getStringsFileFromDirectory(directory)
			val string = strings.find { it.locale.packageName == directory.name } ?: error("ABOBA")
			writeStringInFile(stringsFile, string)
		}
	}

	private fun getAllValuesDirectories(module: Module): List<File> {
		val path = module.externalProjectPath ?: error("Invalid module path: ${module.externalProjectPath}")
		val resDirectoryPath = path + RES_DIRECTORY_PATH
		val directories = File(resDirectoryPath)
			.walk()
			.filter { it.isDirectory && it.name in Locale.packageList() }
			.toList()

		return directories
	}

	private fun getStringsFileFromDirectory(directory: File): File =
		directory
			.walk()
			.find { it.isFile && it.name == STRINGS_FILE_NAME }
			?: throw IllegalArgumentException("No string file in directory ${directory.absolutePath}")

	private fun writeStringInFile(file: File, string: ResourceString) {
		val onlyStringsSubstring = file.readText().substringAfter("<resources>").substringBefore("</resources>")
		val newString = XmlTemplate.stringTemplate(name = string.name, value = string.value)
		val strings = onlyStringsSubstring.split("\n").filter { it.isNotBlank() }.map { it.trim() }

		if (strings.any { it.contains(string.name) }) {
			throw IllegalArgumentException("${string.name} already exist in ${file.path}")
		} else {
			val resultStrings = strings + newString
			val sortedStrings = resultStrings.sorted()
			file.writeText(XmlTemplate.resourceFileTemplate(sortedStrings))
		}
	}
}