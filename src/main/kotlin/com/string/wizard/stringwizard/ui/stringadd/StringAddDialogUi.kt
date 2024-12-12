package com.string.wizard.stringwizard.ui.stringadd

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.domain.entity.NewString
import com.string.wizard.stringwizard.ui.ButtonState

interface StringAddDialogUi {

	fun showTargetModuleSelector(modules: List<Module>)

	fun changeTargetModuleButton(text: String, state: ButtonState)

	fun showNewStrings(strings: List<NewString>)

	fun setCreateFilesVisible(visible: Boolean)

	fun setCreateFileBottomText(text: String, state: BottomTextState)

	fun setErrorText(text: String, state: BottomTextState)

	fun setNewStringName(moduleName: String)
}