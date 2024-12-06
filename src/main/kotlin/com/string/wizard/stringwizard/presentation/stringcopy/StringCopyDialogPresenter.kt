package com.string.wizard.stringwizard.presentation.stringcopy

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.repository.StringRepository
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.stringcopy.StringCopyDialogUi
import com.string.wizard.stringwizard.ui.util.formatResourceString
import org.jetbrains.kotlin.idea.util.sourceRoots

class StringCopyDialogPresenter(private val ui: StringCopyDialogUi, project: Project) {

	private val stringRepository = StringRepository()

	private val filteredModules = project.modules.filter { module ->
		module.sourceRoots.any { !it.path.contains("test", ignoreCase = true) }
	}

	private var selectedSourceModule: Module? = null
	private var selectedTargetModule: Module? = null

	private var selectedString: ResourceString? = null
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
		if (selectedString != null) {
			ui.changeCopyButtonEnabled(true)
		}
	}

	fun onStringSelectionClick() {
		try {
			val module = selectedSourceModule ?: error("Choose source module first!")
			val strings = stringRepository.getStringResList(module, domain)

			ui.showSourceStringSelector(strings)
			ui.hideStringSelectionFailed()
		} catch (e: Exception) {
			ui.showStringSelectionFailed(e)
		}
	}

	fun selectString(string: ResourceString) {
		selectedString = string

		ui.changeSourceStringButton(formatResourceString(string.name, string.value, string.locale), ButtonState.FILLED)
		ui.changeNewStringName(string.name)

		if (selectedTargetModule != null) {
			ui.changeCopyButtonEnabled(true)
		}
	}

	fun copy(newStringName: String) {
		if (newStringName.isNotBlank() && selectedTargetModule != null && selectedSourceModule != null && selectedString != null) {
			try {
				stringRepository.copyString(
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
			} catch (e: Exception) {
				ui.showCopyFailed(e)
			}
		}
	}

	fun selectDomain(domain: Domain) {
		this.domain = domain
	}
}