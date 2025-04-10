package com.string.wizard.stringwizard.data.repository

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.datasource.FileDataSource
import com.string.wizard.stringwizard.data.datasource.ResourceStringDataSource
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.entity.ResourcesPackage
import com.string.wizard.stringwizard.data.util.XmlTemplate
import com.string.wizard.stringwizard.data.util.getLocale
import com.string.wizard.stringwizard.data.util.getPackage
import java.io.File

class StringRepository {

	private val stringDataSource = ResourceStringDataSource()
	private val fileDataSource = FileDataSource()

	fun get(module: Module, domain: Domain, resourcesPackage: ResourcesPackage): List<ResourceString> {
		val allDirectories = fileDataSource.getResourceDirectories(module, domain)
		val defaultDirectory = allDirectories.find { it.name == resourcesPackage.packageName } ?: allDirectories.first()
		val stringsFile = fileDataSource.getStringFile(defaultDirectory, domain)

		return getStrings(stringsFile, defaultDirectory.name, domain)
	}

	private fun getStrings(stringsFile: File, directoryName: String, domain: Domain): List<ResourceString> {
		val locale = ResourcesPackage.findByPackageName(directoryName)?.getLocale(domain)
			?: throw IllegalArgumentException("Unexpected directory locale: ${stringsFile.absolutePath}")

		val defaults = stringDataSource.getDefaults(stringsFile, locale).sortedBy { it.name }

		return defaults
	}

	fun get(module: Module, domain: Domain, stringName: String): List<ResourceString.Default> {
		val valuesDirectories = fileDataSource.getResourceDirectories(module, domain)

		return valuesDirectories.map { directory ->
			val stringsFile = fileDataSource.getStringFile(directory, domain)
			val locale = ResourcesPackage.findByPackageName(directory.name)?.getLocale(domain)
				?: throw IllegalArgumentException("Unexpected directory locale: ${directory.absolutePath}")
			(stringDataSource.get(stringsFile, locale, stringName) as? ResourceString.Default) ?: TODO("Поддержать плюралсы")
		}
	}

	fun write(module: Module, domain: Domain, strings: List<ResourceString.Default>) {
		val directories = fileDataSource.getResourceDirectories(module, domain)

		// TODO: Избавиться от абобы
		directories.forEach { directory ->
			val stringsFile = fileDataSource.getStringFile(directory, domain)
			val string = strings.find { it.locale.getPackage(domain).packageName == directory.name } ?: error("ABOBA")
			writeStringInFile(stringsFile, string)
		}
	}

	/** Данный метод предназначен для сортировки строк в модуле */
	fun sort(module: Module) {
		val directories = fileDataSource.getResourceDirectories(module)
		val domains = Domain.values()

		val domainsStrings = domains.associateWith { getStringFilesForDomain(directories, it) }

		domainsStrings.forEach { (domain, stringFiles) ->
			stringFiles.forEach { file ->
				val locale = ResourcesPackage.findByPackageName(file.parentFile.name)?.getLocale(domain) ?: error("Unexpected directory path ${file.path}")
				val listDefaults = stringDataSource.getDefaults(file, locale).sortedBy { it.name }
				val listPlurals = stringDataSource.getPlurals(file, locale).sortedBy { it.name }

				stringDataSource.write(file, listPlurals + listDefaults)
			}
		}
	}

	private fun getStringFilesForDomain(directories: List<File>, domain: Domain): List<File> =
		directories.mapNotNull { directory ->
			runCatching {
				fileDataSource.getStringFile(directory, domain)
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
}