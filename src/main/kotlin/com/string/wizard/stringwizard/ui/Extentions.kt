package com.string.wizard.stringwizard.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.string.wizard.stringwizard.ui.util.formatModuleName
import com.string.wizard.stringwizard.ui.component.ButtonWithLabel
import org.jetbrains.kotlin.idea.util.sourceRoots
import javax.swing.JButton

fun JButton.changeModuleButton(text: String, state: ButtonState) {
	this.text = formatModuleName(text)
	icon = when (state) {
		ButtonState.EMPTY  -> AllIcons.General.Add
		ButtonState.FILLED -> AllIcons.Nodes.Module
	}
}

fun ButtonWithLabel.changeButton(text: String, state: ButtonState) {
	this.text = formatModuleName(text)
	icon = when (state) {
		ButtonState.EMPTY  -> AllIcons.General.Add
		ButtonState.FILLED -> AllIcons.Nodes.Module
	}
}

fun Project.takeMainModules(): List<Module> = this.modules.filter { module ->
	module.sourceRoots.any { !it.path.contains("test", ignoreCase = true) }
}