package com.string.wizard.stringwizard.ui.stringadd

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.ui.ButtonState

interface StringAddDialogUi {

	fun showTargetModuleSelector(modules: List<Module>)

	fun changeTargetModuleButton(text: String, state: ButtonState)
}