package com.string.wizard.stringwizard.ui.stringcopy

import com.android.tools.idea.projectsystem.isMainModule
import com.android.tools.idea.projectsystem.isUnitTestModule
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import org.jetbrains.kotlin.idea.base.util.isAndroidModule

class StringCopyDialogPresenter(private val ui: StringCopyDialogUi, private val project: Project) {

    private val filteredModules = project.modules
            .toList()
            .filter { it.isAndroidModule() && it.isMainModule() && !it.isUnitTestModule() }
            .sortedBy { it.name }

    fun onModulesChooserClick() {
        ui.showModulesSelector(filteredModules)
    }
}