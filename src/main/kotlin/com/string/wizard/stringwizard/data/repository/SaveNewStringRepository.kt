package com.string.wizard.stringwizard.data.repository

import com.intellij.openapi.module.Module
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

	override fun add(targetModule: Module, name: String, defaultRuValue: String, defaultEnValue: String) {
		val modulePath = getModulePath(targetModule)
		val valuesList = getResourceFiles(modulePath)
		if (valuesList.isNotEmpty()) {
			valuesList.forEach { valueDir ->
				writeNewString(
					file = valueDir,
					name = name,
					defaultRuValue = defaultRuValue,
					defaultEnValue = defaultEnValue,
				)
			}
		} else {
			throw IllegalStateException() //TODO() Добавить возможность создавать директории
		}
	}

	private fun writeNewString(file: File, name: String, defaultRuValue: String, defaultEnValue: String) {
		val value = File(file.absolutePath).walk().find { it.isFile && it.name.startsWith("strings") && it.name.endsWith(".xml") }

		if (value != null) {
			writeStringInTargetRes(value, name, defaultRuValue)
		} else {
			throw IllegalStateException() //TODO() Добавить возможность создавать директории
		}
	}

	private fun getResourceFiles(modulePath: String): List<File> {
		val resDirectoryPath = modulePath + RES_DIRECTORY_PATH
		return File(resDirectoryPath).walk().filter { it.isDirectory && it.name == "values" }.toList()
	}

	private fun getModulePath(module: Module) =
		module.externalProjectPath ?: error("Invalid module path: ${module.externalProjectPath}")

	private fun writeStringInTargetRes(file: File, name: String, string: String) {
		val onlyStringsSubstring = file.readText().substringAfter("<resources>").substringBefore("</resources>")
		val newString = "<string name=\"$name\">$string</string>"
		val strings = onlyStringsSubstring.split("\n").filter { it.isNotBlank() }.map { it.trim() }

		if (strings.any { it.contains(name) }) {
			throw IllegalArgumentException("$name already exist in ${file.path}")
		} else {
			val resultStrings = strings + newString
			file.writeText(generateNewResFileContent(resultStrings))
		}
	}

	private fun generateNewResFileContent(strings: List<String>): String =
		"""
		|<?xml version=\"1.0\" encoding=\"utf-8\"?>
		|<resources>
		|${strings.joinToString(separator = SEPARATOR) { "\t$it" }}
		|</resources>
	""".trimMargin()
}