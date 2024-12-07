package com.string.wizard.stringwizard.ui.component

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.ui.components.JBLabel
import com.string.wizard.stringwizard.ui.util.formatModuleName
import java.awt.Component
import javax.swing.JList
import javax.swing.ListCellRenderer

class ModuleListRenderer : JBLabel(), ListCellRenderer<Module> {

	override fun getListCellRendererComponent(
		list: JList<out Module>?,
		value: Module?,
		index: Int,
		isSelected: Boolean,
		cellHasFocus: Boolean
	): Component {
		text = value?.name?.let { formatModuleName(it) }.orEmpty()
		icon = AllIcons.Nodes.Module

		return this
	}
}