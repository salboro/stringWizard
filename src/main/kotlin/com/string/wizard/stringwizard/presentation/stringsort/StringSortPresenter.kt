package com.string.wizard.stringwizard.presentation.stringsort

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.string.wizard.stringwizard.domain.usecase.SortStringUseCase
import com.string.wizard.stringwizard.ui.addfromexcel.AttentionTextState
import com.string.wizard.stringwizard.ui.stringsort.StringSortDialogUi
import com.string.wizard.stringwizard.ui.takeMainModules

class StringSortPresenter(private val ui: StringSortDialogUi, project: Project) {

	private val filteredModules = project.takeMainModules()

	private val sortStringUseCase = SortStringUseCase()

	private var targetModule: Module? = null

	fun onTargetModuleSelectorClick() {
		val targetModuleList = targetModule?.let { filteredModules - it } ?: filteredModules
		ui.showTargetModuleSelector(targetModuleList)
	}

	fun changeModule(module: Module) {
		targetModule = module
		ui.hideAttentionText()
	}

	fun sortString() {
		try {
			val targetModule = targetModule ?: error("Choose Module")
			sortStringUseCase(targetModule)
			ui.setAttentionText("Success", AttentionTextState.SUCCESS)
		} catch (e: Exception) {
			ui.setAttentionText(e.message ?: "Unknown Exception", AttentionTextState.ERROR)
		}
	}
}