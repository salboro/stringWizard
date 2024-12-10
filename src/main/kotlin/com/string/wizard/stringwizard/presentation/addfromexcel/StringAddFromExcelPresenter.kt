package com.string.wizard.stringwizard.presentation.addfromexcel

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.vfs.VirtualFile
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.exception.StringFileException
import com.string.wizard.stringwizard.data.util.getDefaultLocale
import com.string.wizard.stringwizard.data.util.getLocales
import com.string.wizard.stringwizard.domain.addfromexcel.interactor.StringAddFromExcelInteractor
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.addfromexcel.AttentionTextState
import com.string.wizard.stringwizard.ui.addfromexcel.StringAddFromExcelUi
import com.string.wizard.stringwizard.ui.takeMainModules
import com.string.wizard.stringwizard.ui.util.formatExcelString
import org.jetbrains.kotlin.idea.util.sourceRoots
import java.io.File

class StringAddFromExcelPresenter(private val ui: StringAddFromExcelUi, project: Project) {

	private val interactor = StringAddFromExcelInteractor()

	private val filteredModules = project.takeMainModules()

	private var excelFile: File? = null
	private var excelStrings: List<ExcelString>? = null
	private var filteredExcelStrings: List<ExcelString>? = null
	private var selectedTargetModule: Module? = null
	private var domain = Domain.DP

	fun chooseFile(file: VirtualFile) {
		excelFile = File(file.path)

		ui.enableExcelString(true)
		ui.changeExcelString("")
		ui.hideExcelStrings()
	}

	fun onExcelStringSelectClick() {
		try {
			val excelFile = excelFile ?: return
			val strings = interactor.getExcelStrings(excelFile, Locale.RU)

			ui.hideExcelStrings()
			ui.showStringSelector(strings)
		} catch (e: Exception) {
			ui.setAttentionText(e.message ?: "Unknown exception", AttentionTextState.ERROR)
			ui.hideExcelStrings()
		}
	}

	fun selectExcelString(string: ExcelString) {
		val excelFile = excelFile ?: return
		val stringsForAllLocales = interactor.getExcelStrings(excelFile, string.position)
		val domainLocales = domain.getLocales()
		val filteredExcelStrings = stringsForAllLocales.filter { it.locale in domainLocales }

		excelStrings = stringsForAllLocales
		this.filteredExcelStrings = filteredExcelStrings

		ui.changeExcelString(formatExcelString(string.value, string.position, string.locale))
		ui.showExcelStrings(filteredExcelStrings)
	}

	fun onTargetModuleSelectorClick() {
		filteredModules
		ui.showTargetModuleSelector(filteredModules)
	}

	fun selectTargetModule(module: Module) {
		selectedTargetModule = module

		ui.changeTargetModuleButton(module.name, ButtonState.FILLED)
		ui.changeNewStringName(module.name)
		ui.setCreateFilesButtonVisible(false)
	}

	fun add(newStringName: String) {
		try {
			val excelStrings = filteredExcelStrings ?: error("Excel string not selected!")
			val selectedModule = selectedTargetModule ?: error("Module not selected!")

			interactor.writeStrings(excelStrings, domain, newStringName, selectedModule)
			ui.setAttentionText("Success!", AttentionTextState.SUCCESS)
			ui.setCreateFilesButtonVisible(false)
		} catch (e: StringFileException) {
			ui.setAttentionText(e.message ?: "Unknown exception", AttentionTextState.ERROR)
			ui.setCreateFilesButtonVisible(true)
		} catch (e: Exception) {
			ui.setAttentionText(e.message ?: "Unknown exception", AttentionTextState.ERROR)
		}
	}

	fun onDispose() {
		interactor.closeExcel()
	}

	fun selectDomain(domain: Domain) {
		this.domain = domain
		val excelStrings = excelStrings ?: return
		val domainLocales = domain.getLocales()
		val filteredStrings = excelStrings.filter { it.locale in domainLocales }

		this.filteredExcelStrings = filteredStrings

		ui.showExcelStrings(filteredStrings)
		ui.setCreateFilesButtonVisible(false)
	}

	fun changeExcelStringValue(newValue: String, stringIndex: Int) {
		val excelStrings = excelStrings ?: return
		val changedString = excelStrings[stringIndex]

		val newStrings = excelStrings.mapIndexed { index, string ->
			if (index == stringIndex || (string.locale.getDefaultLocale() == changedString.locale && string.value == changedString.value)) {
				string.copy(value = newValue)
			} else {
				string
			}
		}

		this.excelStrings = newStrings
		ui.showExcelStrings(newStrings)
	}

	fun createStringFiles() {
		val selectedTargetModule = selectedTargetModule ?: error("Module not selected!")

		try {
			interactor.createStringFiles(selectedTargetModule, domain)
			ui.setCreateFilesButtonVisible(false)
			ui.setAttentionText("Files successfully created!", AttentionTextState.SUCCESS)
		} catch (e: Exception) {
			ui.setAttentionText(e.message ?: "Unknown error", AttentionTextState.ERROR)
		}
	}
}