package com.string.wizard.stringwizard.presentation.stringcopy

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.exception.StringFileException
import com.string.wizard.stringwizard.domain.stringcopy.interactor.StringCopyInteractor
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.stringcopy.StringCopyDialogUi
import com.string.wizard.stringwizard.ui.takeMainModules
import com.string.wizard.stringwizard.ui.util.formatResourceString

class StringCopyDialogPresenter(private val ui: StringCopyDialogUi, project: Project) {

	private val interactor = StringCopyInteractor()

	private val filteredModules = project.takeMainModules()

	private var selectedSourceModule: Module? = null
	private var selectedTargetModule: Module? = null

	private var selectedString: ResourceString.Default? = null
	private var domain = Domain.DP

	fun onModulesChooserClick() {
		ui.showSourceModulesSelector(filteredModules)
	}

	fun onTargetModuleSelectorClick() {
		val targetModuleList = selectedSourceModule?.let { filteredModules - it } ?: filteredModules
		ui.showTargetModuleSelector(targetModuleList)
	}

	fun selectModule(module: Module) {
		selectedSourceModule = module
		selectedString = null

		ui.changeSourceModuleButton(module.name, ButtonState.FILLED)
		ui.changeSourceStringButton(text = "", ButtonState.EMPTY)
		ui.disableNewStringName()
		ui.changeCopyButtonEnabled(false)
	}

	fun selectTargetModule(module: Module) {
		selectedTargetModule = module

		ui.changeTargetModuleButton(module.name, ButtonState.FILLED)
		ui.setCreateFilesButtonVisible(false)
		if (selectedString != null) {
			ui.changeCopyButtonEnabled(true)
		}
	}

	fun onStringSelectionClick() {
		try {
			val module = selectedSourceModule ?: error("Choose source module first!")
			val strings = interactor.getBaseStrings(module, domain).filterIsInstance<ResourceString.Default>()

			ui.showSourceStringSelector(strings)
			ui.hideStringSelectionFailed()
		} catch (e: Exception) {
			ui.showStringSelectionFailed(e)
		}
	}

	fun selectString(string: ResourceString.Default) {
		selectedString = string

		ui.changeSourceStringButton(formatResourceString(string.name, string.value, string.locale ?: Locale.RU), ButtonState.FILLED)
		ui.changeNewStringName(string.name)

		if (selectedTargetModule != null) {
			ui.changeCopyButtonEnabled(true)
		}
	}

	fun copy(newStringName: String) {
		if (newStringName.isNotBlank() && selectedTargetModule != null && selectedSourceModule != null && selectedString != null) {
			try {
				interactor.copyString(
					requireNotNull(selectedSourceModule),
					requireNotNull(selectedTargetModule),
					requireNotNull(selectedString?.name),
					newStringName,
					domain
				)

				ui.showSuccess(
					requireNotNull(selectedSourceModule?.name),
					requireNotNull(selectedTargetModule?.name),
					requireNotNull(selectedString?.name),
					newStringName
				)
				ui.setCreateFilesButtonVisible(false)
			} catch (e: StringFileException) {
				ui.showCopyFailed(e)
				ui.setCreateFilesButtonVisible(true)
			} catch (e: Exception) {
				ui.showCopyFailed(e)
			}
		}
	}

	fun selectDomain(domain: Domain) {
		this.domain = domain
		ui.setCreateFilesButtonVisible(false)
	}

	fun createStringFiles() {
		val selectedTargetModule = selectedTargetModule ?: error("Module not selected!")

		try {
			interactor.createStringFiles(selectedTargetModule, domain)
			ui.setCreateFilesButtonVisible(false)
			ui.showSuccessCreateFiles()
		} catch (e: Exception) {
			ui.showCopyFailed(e)
		}
	}
}