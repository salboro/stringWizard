package com.string.wizard.stringwizard.data.repository

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.entity.ResourcesPackage
import com.string.wizard.stringwizard.data.util.XmlTemplate
import com.string.wizard.stringwizard.data.util.getLocale
import com.string.wizard.stringwizard.data.util.getPackage
import com.string.wizard.stringwizard.data.util.getResourcesPackageList
import com.string.wizard.stringwizard.data.util.getStringFileName
import org.jetbrains.kotlin.idea.base.projectStructure.externalProjectPath
import java.io.File

class StringRepository {

	private companion object {

		const val RES_DIRECTORY_PATH = "/src/main/res/"
	}

	fun getStringResList(module: Module, domain: Domain): List<ResourceString> {
		val allDirectories = getAllValuesDirectories(module, domain)
		val defaultDirectory = allDirectories.find { it.name == ResourcesPackage.BASE.packageName } ?: allDirectories.first()
		val stringsFile = getStringsFileFromDirectory(defaultDirectory, domain)

		return getStrings(stringsFile, defaultDirectory.name, domain)
	}

	private fun getStrings(stringsFile: File, directoryName: String, domain: Domain): List<ResourceString> {
		val resText = stringsFile.readText()
		val locale = ResourcesPackage.findByPackageName(directoryName)?.getLocale(domain)
			?: throw IllegalArgumentException("Unexpected directory locale: ${stringsFile.absolutePath}")

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

	fun copyString(sourceModule: Module, targetModule: Module, stringSourceName: String, stringTargetName: String, domain: Domain) {
		val sourceStrings = getAllLocaleStrings(stringSourceName, sourceModule, domain)
		val stringsWithNewName = sourceStrings.map { it.copy(name = stringTargetName) }

		writeNewStringsInAllLocale(targetModule, stringsWithNewName, domain)
	}

	private fun getAllLocaleStrings(stringName: String, module: Module, domain: Domain): List<ResourceString> {
		val valuesDirectories = getAllValuesDirectories(module, domain)

		return valuesDirectories.map { directory ->
			val stringsFile = getStringsFileFromDirectory(directory, domain)
			val locale = ResourcesPackage.findByPackageName(directory.name)?.getLocale(domain)
				?: throw IllegalArgumentException("Unexpected directory locale: ${directory.absolutePath}")
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

	fun writeNewStringsInAllLocale(module: Module, strings: List<ResourceString>, domain: Domain) {
		val directories = getAllValuesDirectories(module, domain)

		directories.forEach { directory ->
			val stringsFile = getStringsFileFromDirectory(directory, domain)
			val string = strings.find { it.locale.getPackage(domain).packageName == directory.name } ?: error("ABOBA")
			writeStringInFile(stringsFile, string)
		}
	}

	private fun getAllValuesDirectories(module: Module, domain: Domain): List<File> {
		assertModule(module, domain)

		val path = module.externalProjectPath ?: error("Invalid module path: ${module.externalProjectPath}")
		val resDirectoryPath = path + RES_DIRECTORY_PATH
		val directories = File(resDirectoryPath)
			.walk()
			.filter { it.isDirectory && it.name in domain.getResourcesPackageList().map(ResourcesPackage::packageName) }
			.toList()

		return directories
	}

	private fun assertModule(module: Module, domain: Domain) {
		val path = module.externalProjectPath ?: error("Invalid module path: ${module.externalProjectPath}")
		val resDirectoryPath = path + RES_DIRECTORY_PATH
		val stringsFiles = File(resDirectoryPath)
			.walk()
			.filter { it.isFile && it.name.contains("strings") && it.name != "strings_untranslatable.xml" }
			.toList()
		val invalidFile = stringsFiles.find { it.name != domain.getStringFileName() }

		when {
			stringsFiles.isEmpty()                                    -> error("No strings in module ${module.name}")
			invalidFile != null                                       -> error("File name ${invalidFile.name} not match domain ${domain.name}")
			stringsFiles.size < domain.getResourcesPackageList().size -> error("Not enough resources packages for domain ${domain.name}")
		}
	}

	private fun getStringsFileFromDirectory(directory: File, domain: Domain): File =
		directory
			.walk()
			.find { it.isFile && it.name == domain.getStringFileName() }
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