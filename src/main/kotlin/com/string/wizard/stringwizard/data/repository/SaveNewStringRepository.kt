package com.string.wizard.stringwizard.data.repository

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.entity.ResourcesPackage
import com.string.wizard.stringwizard.data.util.DirectoryPath
import com.string.wizard.stringwizard.data.util.XmlTemplate
import com.string.wizard.stringwizard.data.util.getDefaultLocale
import com.string.wizard.stringwizard.data.util.getLocale
import org.jetbrains.kotlin.idea.base.projectStructure.externalProjectPath
import java.io.File

interface NewStringRepository {

	fun add(
		targetModule: Module,
		name: String,
		defaultRuValue: String,
		defaultEnValue: String,
	)
}

class NewStringRepositoryImpl : NewStringRepository {

	private val listDpValues = ResourcesPackage.values()

	override fun add(targetModule: Module, name: String, defaultRuValue: String, defaultEnValue: String) {
		val modulePath = getModulePath(targetModule)
		val resPath = "$modulePath${DirectoryPath.RES_DIRECTORY}"
		var valuesList = try {
			getValuesList(resPath)
		} catch (e: Exception) {
			throw Exception("Ошибка при получении списка")
		} //  TODO Подумать как обработать кейс c созданными папками не полностью

		if (valuesList.isEmpty()) {
			createStringsFiles(modulePath)
			valuesList = getValuesList(modulePath)
		}

		valuesList.forEach { valueDir ->
			writeNewString(
				file = valueDir,
				name = name,
				defaultRuValue = defaultRuValue,
				defaultEnValue = defaultEnValue,
			)
		}
	}

	private fun writeNewString(
		file: File,
		name: String,
		defaultRuValue: String,
		defaultEnValue: String
	) {
		var stringsFile = getStringsFile(file)
		if (stringsFile == null) {
			try {
				createNewStringsFile(file.absolutePath)
				stringsFile = File(file.absolutePath).walk().find { it.isFile && it.name.startsWith("strings") && it.name.endsWith(".xml") }
			} catch (_: Exception) {
				throw Exception("Не удалось создать файл ${file.absolutePath}/strings.xml")
			}
		}

		stringsFile?.let {
			if (ResourcesPackage.findByPackageName(file.name)?.getLocale(Domain.DP)?.getDefaultLocale() == Locale.EN) {
				writeStringInTargetRes(it, name, defaultEnValue)
			} else {
				writeStringInTargetRes(it, name, defaultRuValue)
			}
		} ?: throw Exception("Не нашли строковой файл в ${file.absolutePath}")
	}

	private fun getStringsFile(file: File) =
		File(file.absolutePath).walk().find { it.isFile && it.name.startsWith("strings") && it.name.endsWith(".xml") }

	private fun getValuesList(resPath: String): List<File> =
		File(resPath).walk().filter { it.isDirectory && it.name.startsWith("values") }.toList()

	private fun createStringsFiles(modulePath: String) {
		val resPath = "$modulePath${DirectoryPath.RES_DIRECTORY}"
		try {
			File(resPath).mkdir()
		} catch (e: Exception) {
			throw Exception("не добавляются папка с ресурсами + ${e.message}")
		}

		listDpValues.forEach {
			try {
				File("$resPath${it.packageName}").mkdir()
			} catch (e: Exception) {
				throw Exception("не добавляются values + ${e.message}")
			}

			createNewStringsFile(resPath + it)
		}
	}

	private fun createNewStringsFile(valueAbsolutePath: String) {
		try {
			File("$valueAbsolutePath/strings.xml").createNewFile()
		} catch (e: Exception) {
			throw Exception("не добавляются строки + ${e.message}")
		}
	}

	private fun getModulePath(module: Module) =
		module.externalProjectPath ?: error("Invalid module path: ${module.externalProjectPath}")

	private fun writeStringInTargetRes(file: File, name: String, string: String) {
		val onlyStringsSubstring = file.readText().substringAfter("<resources>").substringBefore("</resources>")
		val strings = onlyStringsSubstring.split("\n").filter { it.isNotBlank() }.map { it.trim() }

		if (strings.any { it.contains(name) }) {
			throw IllegalArgumentException("$name already exist in ${file.path}")
		} else {
			val resultStrings = strings + XmlTemplate.stringTemplate(name, string)
			file.writeText(XmlTemplate.resourceFileTemplate(resultStrings))
		}
	}
}