package com.string.wizard.stringwizard.ui.stringcopy

import com.android.tools.idea.projectsystem.isMainModule
import com.android.tools.idea.projectsystem.isUnitTestModule
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.repository.StringRepository
import com.string.wizard.stringwizard.ui.util.formatResourceString
import com.string.wizard.stringwizard.ui.ButtonState
import org.jetbrains.kotlin.idea.base.util.isAndroidModule


class StringCopyDialogPresenter(private val ui: StringCopyDialogUi, project: Project) {

    private val stringRepository = StringRepository()

    private val filteredModules = project.modules
            .toList()
            .filter { it.isAndroidModule() && it.isMainModule() && !it.isUnitTestModule() }
            .sortedBy { it.name }

    private var selectedModule: Module? = null
    private var selectedTargetModule: Module? = null

    private var selectedString: ResourceString? = null

    fun onModulesChooserClick() {
        ui.showSourceModulesSelector(filteredModules)
    }

    fun onTargetModuleSelectorClick() {
        val targetModuleList = selectedModule?.let { filteredModules - it } ?: filteredModules
        ui.showTargetModuleSelector(targetModuleList)
    }

    fun selectModule(module: Module) {
        selectedModule = module

        ui.changeSourceModuleButton(module.name, ButtonState.FILLED)
        ui.changeSourceStringButton(text = "", ButtonState.EMPTY)
        ui.disableNewStringName()
    }

    fun selectTargetModule(module: Module) {
        selectedTargetModule = module

        ui.changeTargetModuleButton(module.name, ButtonState.FILLED)
    }

    fun onStringSelectionClick() {
        val module = selectedModule ?: return
        val strings = stringRepository.getStringResList(module).ifEmpty { listOf(ResourceString("awd", "awd", "awd")) }

        ui.showSourceStringSelector(strings)
    }

    fun selectString(string: ResourceString) {
        selectedString = string

        ui.changeSourceStringButton(formatResourceString(string.name, string.value), ButtonState.FILLED)
        ui.changeNewStringName(string.name)
    }
}