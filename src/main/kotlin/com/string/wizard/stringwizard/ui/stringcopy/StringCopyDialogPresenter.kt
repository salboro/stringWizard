package com.string.wizard.stringwizard.ui.stringcopy

import com.android.tools.idea.projectsystem.isMainModule
import com.android.tools.idea.projectsystem.isUnitTestModule
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import org.jetbrains.kotlin.idea.base.util.isAndroidModule


class StringCopyDialogPresenter(private val ui: StringCopyDialogUi, project: Project) {

    private val filteredModules = project.modules
            .toList()
            .filter { it.isAndroidModule() && it.isMainModule() && !it.isUnitTestModule() }
            .sortedBy { it.name }

    private var selectedModule: Module? = null

    fun onModulesChooserClick() {
        ui.showModulesSelector(filteredModules)
    }

    fun selectModule(module: Module) {
        selectedModule = module

        ui.changeModuleButton(module.name, ButtonState.FILLED)
    }
}