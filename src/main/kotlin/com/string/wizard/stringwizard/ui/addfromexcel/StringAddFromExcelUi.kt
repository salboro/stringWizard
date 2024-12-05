package com.string.wizard.stringwizard.ui.addfromexcel

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.ui.ButtonState

interface StringAddFromExcelUi {

	fun setAttentionText(text: String, state: AttentionTextState)

	fun enableExcelString(enabled: Boolean)

	fun showStringSelector(strings: List<ExcelString>)

	fun changeExcelString(string: String)

	fun showExcelStrings(strings: List<ExcelString>)

	fun hideExcelStrings()

	fun showTargetModuleSelector(modules: List<Module>)

	fun changeTargetModuleButton(text: String, state: ButtonState)

	fun changeNewStringName(text: String)
}