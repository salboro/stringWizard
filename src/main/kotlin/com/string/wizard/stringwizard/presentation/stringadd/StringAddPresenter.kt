package com.string.wizard.stringwizard.presentation.stringadd

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.exception.StringFileException
import com.string.wizard.stringwizard.data.util.getDefaultLocale
import com.string.wizard.stringwizard.data.util.getLocales
import com.string.wizard.stringwizard.data.util.getSortedLocales
import com.string.wizard.stringwizard.domain.entity.NewString
import com.string.wizard.stringwizard.domain.stringadd.interactor.AddStringInteractor
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.stringadd.BottomTextState
import com.string.wizard.stringwizard.ui.stringadd.StringAddDialogUi
import com.string.wizard.stringwizard.ui.takeMainModules

class StringAddPresenter(private val ui: StringAddDialogUi, project: Project) {

	private val addStringInteractor = AddStringInteractor()

	private val filteredModules = project.takeMainModules()

	private var selectedModule: Module? = null

	private var domain: Domain = Domain.DP

	private var newStrings: List<NewString> = domain.createEmptyStrings()

	private var filteredStrings: List<NewString> = newStrings

	private fun Domain.createEmptyStrings(): List<NewString> =
		getSortedLocales().mapIndexed { index, locale ->
			NewString("", index, locale)
		}

	fun onTargetModuleSelectorClick() {
		val targetModuleList = selectedModule?.let { filteredModules - it } ?: filteredModules
		ui.showTargetModuleSelector(targetModuleList)
	}

	fun selectModule(module: Module) {
		selectedModule = module

		ui.changeTargetModuleButton(module.name, ButtonState.FILLED)
		ui.setNewStringName(module.name)
		ui.showNewStrings(filteredStrings)
	}

	fun onAddButtonClick(newStringName: String) {
		try {
			val selectedModule = selectedModule ?: error("Module not selected!")
			val filteredList = filteredStrings
			if (filteredList.any { it.value.isEmpty() }) {
				error("Some string is Empty")
			}
			addStringInteractor.writeStrings(selectedModule, domain, filteredList, newStringName)

			ui.setAttentionText("Success!", BottomTextState.SUCCESS)
			ui.setCreateFilesVisible(false)
		} catch (e: StringFileException) {
			ui.setCreateFileBottomText(e.message ?: "Unknown exception", BottomTextState.ERROR)
			ui.setCreateFilesVisible(true)
		} catch (e: Exception) {
			ui.setAttentionText(e.message ?: "Unknown exception", BottomTextState.ERROR)
		}
	}

	fun selectDomain(domain: Domain) {
		this.domain = domain
		val filteredStrings = newStrings.filter { it.locale in domain.getLocales() }
		this.filteredStrings = filteredStrings

		if (selectedModule != null) {
			ui.showNewStrings(filteredStrings)
			ui.setCreateFilesVisible(false)
		}
	}

	fun changeStringValue(newValue: String, stringIndex: Int) {
		val changedString = filteredStrings[stringIndex]
		val changedNewStringIndex = newStrings.indexOf(changedString)

		val newStrings = newStrings.mapIndexed { index, string ->
			if (index == changedNewStringIndex || (string.locale.getDefaultLocale() == changedString.locale && string.value == changedString.value)) {
				string.copy(value = newValue)
			} else {
				string
			}
		}

		this.newStrings = newStrings
		this.filteredStrings = newStrings.filter { it.locale in domain.getLocales() }

		ui.showNewStrings(filteredStrings)
	}

	fun createFiles() {
		val selectedTargetModule = selectedModule ?: error("Module not selected!")

		try {
			addStringInteractor.createFiles(selectedTargetModule, domain)
			ui.setCreateFilesVisible(false)
			ui.setCreateFileBottomText("Files successfully created!", BottomTextState.SUCCESS) // TODO поменять нейминг
		} catch (e: Exception) {
			ui.setCreateFileBottomText(e.message ?: "Unknown error", BottomTextState.ERROR)
		}
	}
}