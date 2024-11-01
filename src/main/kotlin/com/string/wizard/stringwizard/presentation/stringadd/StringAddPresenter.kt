package com.string.wizard.stringwizard.presentation.stringadd

import com.android.tools.idea.projectsystem.isMainModule
import com.android.tools.idea.projectsystem.isUnitTestModule
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.string.wizard.stringwizard.data.repository.NewStringRepositoryImpl
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.stringadd.StringAddDialogUi
import org.jetbrains.kotlin.idea.base.util.isAndroidModule

class StringAddPresenter(private val ui: StringAddDialogUi, project: Project) {

	private val newStringRepository = NewStringRepositoryImpl()

	private val filteredModules = project.modules
		.toList()
		.filter { it.isAndroidModule() && it.isMainModule() && !it.isUnitTestModule() }
		.sortedBy { it.name }

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