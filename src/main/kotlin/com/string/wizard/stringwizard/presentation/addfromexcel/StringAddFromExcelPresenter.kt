package com.string.wizard.stringwizard.presentation.addfromexcel

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.vfs.VirtualFile
import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.repository.ExcelRepository
import com.string.wizard.stringwizard.data.repository.StringRepository
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.addfromexcel.StringAddFromExcelUi
import com.string.wizard.stringwizard.ui.util.formatExcelString
import org.jetbrains.kotlin.idea.util.sourceRoots
import java.io.File

class StringAddFromExcelPresenter(private val ui: StringAddFromExcelUi, project: Project) {

	private val excelRepository = ExcelRepository()
	private val stringRepository = StringRepository()
	private val stringConverter = StringConverter()

	private val filteredModules = project.modules.filter { module ->
		module.sourceRoots.any { !it.path.contains("test", ignoreCase = true) }
	}

	private var excelFile: File? = null
	private var excelStrings: List<ExcelString>? = null
	private var selectedTargetModule: Module? = null

	fun chooseFile(file: VirtualFile) {
		excelFile = File(file.path)

		ui.enableExcelString(true)
		ui.changeExcelString("")
		ui.hideExcelStrings()
	}

	fun onExcelStringSelectClick() {
		try {
			val excelFile = excelFile ?: return
			val strings = excelRepository.getStringsByLocale(excelFile, Locale.RU)

			ui.hideExcelStrings()
			ui.showStringSelector(strings)
		} catch (e: Exception) {
			ui.showDebugText(e.message ?: "Unknown exception")
			ui.hideExcelStrings()
		}
	}

	fun selectExcelString(string: ExcelString) {
		val excelFile = excelFile ?: return
		val stringsForAllLocales = excelRepository.getStringsForAllLocale(excelFile, string)
		excelStrings = stringsForAllLocales
		ui.changeExcelString(formatExcelString(string.value, string.position, string.locale))
		ui.showExcelStrings(stringsForAllLocales)
	}

	fun onTargetModuleSelectorClick() {
		filteredModules
		ui.showTargetModuleSelector(filteredModules)
	}

	fun selectTargetModule(module: Module) {
		selectedTargetModule = module

		ui.changeTargetModuleButton(module.name, ButtonState.FILLED)
		ui.changeNewStringName(module.name)
	}

	fun add(newStringName: String) {
		try {
			val excelStrings = excelStrings ?: error("Excel string not selected!")
			val selectedModule = selectedTargetModule ?: error("Module not selected!")
			val resourcesStrings = excelStrings.map { stringConverter.convert(it, newStringName) }

			stringRepository.writeNewStringsInAllLocale(selectedModule, resourcesStrings)
			ui.showDebugText("Success!")
		} catch (e: Exception) {
			ui.showDebugText(e.message ?: "Unknown exception")
		}
	}
}