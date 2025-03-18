package com.string.wizard.stringwizard.ui.renderer

import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBLabel
import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.ui.util.formatExcelString
import java.awt.Component
import javax.swing.JList
import javax.swing.ListCellRenderer

class ExcelStringListRenderer : JBLabel(), ListCellRenderer<ExcelString> {

	override fun getListCellRendererComponent(
		list: JList<out ExcelString>?,
		value: ExcelString?,
		index: Int,
		isSelected: Boolean,
		cellHasFocus: Boolean
	): Component {
		text = if (value != null) {
			formatExcelString(value = value.value, position = value.position, locale = value.locale)
		} else {
			"Invalid"
		}
		icon = AllIcons.FileTypes.XsdFile

		return this
	}
}