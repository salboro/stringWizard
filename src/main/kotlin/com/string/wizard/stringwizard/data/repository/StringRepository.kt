package com.string.wizard.stringwizard.data.repository

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.datasource.ResourceStringDataSource
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.entity.ResourcesPackage
import com.string.wizard.stringwizard.data.exception.StringFileException
import com.string.wizard.stringwizard.data.util.DirectoryPath
import com.string.wizard.stringwizard.data.util.XmlTemplate
import com.string.wizard.stringwizard.data.util.getLocale
import com.string.wizard.stringwizard.data.util.getPackage
import com.string.wizard.stringwizard.data.util.getResourcesPackageList
import com.string.wizard.stringwizard.data.util.getStringFileName
import org.jetbrains.kotlin.idea.base.projectStructure.externalProjectPath
import java.io.File

class StringRepository {

	private val stringDataSource = ResourceStringDataSource()

	fun get(module: Module, domain: Domain, resourcesPackage: ResourcesPackage): List<ResourceString> {
		assertModule(module, domain)

		val allDirectories = getResourcesDirectories(module) { it.name in domain.getResourcesPackageList().map(ResourcesPackage::packageName) }
		val defaultDirectory = allDirectories.find { it.name == resourcesPackage.packageName } ?: allDirectories.first()
		val stringFileName = domain.getStringFileName()
		val stringsFile = getFileFromDirectory(defaultDirectory, stringFileName) { it.name == stringFileName }

		return getStrings(stringsFile, defaultDirectory.name, domain)
	}

	private fun getStrings(stringsFile: File, directoryName: String, domain: Domain): List<ResourceString> {
		val locale = ResourcesPackage.findByPackageName(directoryName)?.getLocale(domain)
			?: throw IllegalArgumentException("Unexpected directory locale: ${stringsFile.absolutePath}")

		val defaults = stringDataSource.getDefaults(stringsFile, locale).sortedBy { it.name }

		return defaults
	}

	fun get(module: Module, domain: Domain, stringName: String): List<ResourceString.Default> {
		assertModule(module, domain)

		val valuesDirectories = getResourcesDirectories(module) { it.name in domain.getResourcesPackageList().map(ResourcesPackage::packageName) }

		return valuesDirectories.map { directory ->
			val stringFileName = domain.getStringFileName()
			val stringsFile = getFileFromDirectory(directory, stringFileName) { it.name == stringFileName }
			val locale = ResourcesPackage.findByPackageName(directory.name)?.getLocale(domain)
				?: throw IllegalArgumentException("Unexpected directory locale: ${directory.absolutePath}")
			ResourceString.Default(name = stringName, value = getStringValue(stringsFile, stringName), locale = locale)
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

	fun write(module: Module, domain: Domain, strings: List<ResourceString.Default>) {
		assertModule(module, domain)

		val directories = getResourcesDirectories(module) { it.name in domain.getResourcesPackageList().map(ResourcesPackage::packageName) }

		directories.forEach { directory ->
			val stringFileName = domain.getStringFileName()
			val stringsFile = getFileFromDirectory(directory, stringFileName) { it.name == stringFileName }
			val string = strings.find { it.locale.getPackage(domain).packageName == directory.name } ?: error("ABOBA")
			writeStringInFile(stringsFile, string)
		}
	}

	/** Данный метод предназначен для сортировки строк в модуле */
	fun sort(module: Module) {
		val directories = getResourcesDirectories(module) { it.name.contains("values") }
		val domains = Domain.values()

		val domainsStrings = domains.associateWith { getStringFilesForDomain(directories, it) }

		domainsStrings.forEach { (domain, stringFiles) ->
			stringFiles.forEach { file ->
				val locale = ResourcesPackage.findByPackageName(file.parentFile.name)?.getLocale(domain) ?: error("Unexpected directory path ${file.path}")
				val listDefaults = stringDataSource.getDefaults(file, locale).sortedBy { it.name }
				val listPlurals = stringDataSource.getPlurals(file, locale).sortedBy { it.name }

				file.writeText(XmlTemplate.resourceFileTemplateDefault(listDefaults, listPlurals))
			}
		}
	}

	private fun getStringFilesForDomain(directories: List<File>, domain: Domain): List<File> =
		directories.mapNotNull { directory ->
			runCatching {
				getFileFromDirectory(directory, "string") { it.name == domain.getStringFileName() }
			}.getOrNull()
		}

	private fun writeStringInFile(file: File, string: ResourceString) {
		val defaultsList = stringDataSource.getDefaults(file, string.locale).toMutableList()
		val pluralsList = stringDataSource.getPlurals(file, string.locale).toMutableList()

		if (defaultsList.any { it.name == string.name } || pluralsList.any { it.name == string.name }) {
			throw IllegalArgumentException("${string.name} already exist in ${file.path}")
		} else {
			when (string) {
				is ResourceString.Default -> defaultsList.add(string)
				is ResourceString.Plural  -> pluralsList.add(string)
			}

			file.writeText(XmlTemplate.resourceFileTemplateDefault(defaultsList.sortedBy { it.name }, pluralsList.sortedBy { it.name }))
		}
	}

	private fun getResourcesDirectories(module: Module, directoryFilterPredicate: (File) -> Boolean): List<File> {
		val path = module.externalProjectPath ?: error("Invalid module path: ${module.externalProjectPath}")
		val resDirectoryPath = path + DirectoryPath.RES_DIRECTORY
		val directories = File(resDirectoryPath)
			.walk()
			.filter { it.isDirectory && directoryFilterPredicate(it) }
			.toList()

		return directories
	}

	private fun assertModule(module: Module, domain: Domain) {
		val path = module.externalProjectPath ?: error("Invalid module path: ${module.externalProjectPath}")
		val resDirectoryPath = path + DirectoryPath.RES_DIRECTORY
		val stringsFiles = File(resDirectoryPath)
			.walk()
			.filter { it.isFile && it.name.contains("strings") && it.name != "strings_untranslatable.xml" }
			.toList()
		val domainStringsFiles = stringsFiles.filter { it.name == domain.getStringFileName() }

		val errorText = when {
			stringsFiles.isEmpty()                                          -> "No strings in module ${module.name}"
			domainStringsFiles.size < domain.getResourcesPackageList().size -> "Not enough resources packages for domain ${domain.name}"
			else                                                            -> null
		}
		errorText?.let { throw StringFileException(it) }
	}

	private fun getFileFromDirectory(directory: File, fileName: String, filePredicate: (File) -> Boolean): File =
		directory
			.walk()
			.find { it.isFile && filePredicate(it) }
			?: throw IllegalArgumentException("No $fileName file in directory ${directory.absolutePath}")
}