package com.string.wizard.stringwizard.ui.component

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.util.preferredWidth
import com.string.wizard.stringwizard.ui.resources.Dimension.DEFAULT_COMPONENT_WIDTH
import org.jdesktop.swingx.HorizontalLayout
import javax.swing.JPanel

class TextFieldWithLabel(
	labelText: String,
	text: String = "",
	labelWidth: Int = DEFAULT_COMPONENT_WIDTH,
	private val textFieldWidth: Int = DEFAULT_COMPONENT_WIDTH,
) : JPanel(HorizontalLayout()) {

	private val label = JBLabel(labelText)
	private val textField = JBTextField(text)

	var text: String = textField.text
		set(value) {
			val newWidth = textField.graphics.fontMetrics.stringWidth(value) + extraWidth
			textField.preferredWidth = if (newWidth > textFieldWidth) {
				newWidth
			} else {
				textFieldWidth
			}
			textField.text = value
			field = value
		}

	private val extraWidth: Int
		get() = textField.insets.left + textField.insets.right + textField.margin.left + textField.margin.right

	init {
		label.preferredWidth = labelWidth
		textField.preferredWidth = textFieldWidth
		add(label)
		add(textField)
	}

	override fun setEnabled(enabled: Boolean) {
		textField.isEnabled = enabled
		label.isEnabled = enabled
	}
}