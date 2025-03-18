package com.string.wizard.stringwizard.ui.stringsort

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.ui.addfromexcel.AttentionTextState

interface StringSortDialogUi {

	fun setAttentionText(text: String, state: AttentionTextState)

	fun showTargetModuleSelector(modules: List<Module>)

	fun hideAttentionText()
}