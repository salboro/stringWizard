package com.string.wizard.stringwizard.ui.renderer

import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBLabel
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.util.getFirstValue
import com.string.wizard.stringwizard.ui.util.formatResourceString
import java.awt.Component
import javax.swing.JList
import javax.swing.ListCellRenderer

class StringListRenderer : JBLabel(), ListCellRenderer<ResourceString> {

	override fun getListCellRendererComponent(
		list: JList<out ResourceString>?,
		value: ResourceString?,
		index: Int,
		isSelected: Boolean,
		cellHasFocus: Boolean
	): Component {
		text = if (value != null) {
			val stringValue = when (value) {
				is ResourceString.Default -> value.value
				is ResourceString.Plural  -> value.getFirstValue()
			}
			formatResourceString(name = value.name, value = stringValue, locale = value.locale, plurals = value is ResourceString.Plural)
		} else {
			"Invalid"
		}
		icon = AllIcons.FileTypes.Xml

		return this
	}
}