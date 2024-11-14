package com.string.wizard.stringwizard.data.repository

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.util.XmlTemplate
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

private const val RES_DIRECTORY_PATH = "/src/main/res/"

class NewStringRepositoryImpl : NewStringRepository {

	private companion object {

		const val SEPARATOR = "\n"
	}

	private val listDpValues = Locale.packageList()

	override fun add(targetModule: Module, name: String, defaultRuValue: String, defaultEnValue: String) {
		val modulePath = getModulePath(targetModule)
		val resPath = "$modulePath$RES_DIRECTORY_PATH"
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
			if (Locale.isEnLocale(file.name)) {
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
		val resPath = "$modulePath$RES_DIRECTORY_PATH"
		try {
			File(resPath).mkdir()
		} catch (e: Exception) {
			throw Exception("не добавляются папка с ресурсами + ${e.message}")
		}

		listDpValues.forEach {
			try {
				File("$resPath${it}").mkdir()
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