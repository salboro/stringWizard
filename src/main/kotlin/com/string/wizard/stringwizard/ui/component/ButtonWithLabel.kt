package com.string.wizard.stringwizard.ui.component

import com.intellij.ui.components.JBLabel
import com.intellij.ui.util.preferredWidth
import com.intellij.util.ui.JBUI
import com.string.wizard.stringwizard.ui.resources.Dimension.DEFAULT_COMPONENT_WIDTH
import org.jdesktop.swingx.HorizontalLayout
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class ButtonWithLabel(
	labelText: String,
	text: String,
	icon: Icon,
	labelWidth: Int = DEFAULT_COMPONENT_WIDTH,
	buttonWidth: Int = DEFAULT_COMPONENT_WIDTH,
) : JPanel(HorizontalLayout()) {

	private val label = JBLabel(labelText)
	private val button = JButton(text, icon)

	var text: String = button.text
		set(value) {
			button.apply {
				val newWidth = graphics.fontMetrics.stringWidth(value) + extraWidth
				applyNewWidth(newWidth = newWidth, defaultWidth = DEFAULT_COMPONENT_WIDTH)
				text = value
			}
			field = value
		}

	var icon: Icon = button.icon
		set(value) {
			button.icon = value
			field = value
		}

	init {
		label.preferredWidth = labelWidth
		button.apply {
			margin = JBUI.insets(0, 4)
			preferredWidth = buttonWidth
		}
		add(label)
		add(button)
	}

	private val extraWidth: Int
		get() = button.icon.iconWidth + button.iconTextGap + button.insets.left + button.insets.right + button.margin.left + button.margin.right

	override fun setEnabled(enabled: Boolean) {
		button.isEnabled = enabled
		label.isEnabled = enabled
	}

	fun setActionListener(action: () -> Unit) {
		button.addActionListener { action() }
	}

	private fun JComponent.applyNewWidth(newWidth: Int, defaultWidth: Int) {
		preferredWidth = if (newWidth >= defaultWidth) {
			newWidth
		} else {
			defaultWidth
		}
	}
}