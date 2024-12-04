package com.string.wizard.stringwizard.presentation.stringadd

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.string.wizard.stringwizard.data.repository.NewStringRepositoryImpl
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.stringadd.StringAddDialogUi
import org.jetbrains.kotlin.idea.util.sourceRoots

class StringAddPresenter(private val ui: StringAddDialogUi, project: Project) {

	private val newStringRepository = NewStringRepositoryImpl()

	private val filteredModules = project.modules.filter { module ->
		module.sourceRoots.any { !it.path.contains("test", ignoreCase = true) }
	}

	private var selectedModule: Module? = null

	fun onTargetModuleSelectorClick() {
		val targetModuleList = selectedModule?.let { filteredModules - it } ?: filteredModules
		ui.showTargetModuleSelector(targetModuleList)
	}

	fun selectModule(module: Module) {
		selectedModule = module

		ui.changeTargetModuleButton(module.name, ButtonState.FILLED)
	}

	fun onOkButtonClick(stringName: String, defaultRuValue: String, defaultEnValue: String) {
		val module = selectedModule ?: return

		newStringRepository.add(module, stringName, defaultRuValue, defaultEnValue)
	}
}