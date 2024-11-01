package com.string.wizard.stringwizard.ui.stringadd

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.util.preferredWidth
import com.string.wizard.stringwizard.presentation.stringadd.StringAddPresenter
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.changeModuleButton
import com.string.wizard.stringwizard.ui.component.ModuleListRenderer
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class StringAddDialog(project: Project, dialogTitle: String) : DialogWrapper(project), StringAddDialogUi {

	private val presenter = StringAddPresenter(this, project)
	private val dialogPanel = DialogPanel(BorderLayout())

	private val targetModulePanel = JPanel()
	private val targetModuleLabel = JBLabel("Target module: ")
	private val targetModuleButton = JButton("Choose Module", AllIcons.General.Add)

	private val newStringNameLabel = JBLabel("Input string name")
	private val newStringNameInput = JBTextField()

	private val newStringValueRuLabel = JBLabel("Input Ru default value")
	private val newStringValueRuInput = JBTextField()

	private val newStringValueEnLabel = JBLabel("Input En default value")
	private val newStringValueEnInput = JBTextField()

	private var targetModule: Module? = null

	init {
		title = dialogTitle
		init()
	}

	override fun createCenterPanel(): JComponent {
		targetModuleButton.addActionListener {
			presenter.onTargetModuleSelectorClick()
		}

		val gridBagLayout = GridBagLayout()
		val gridBagConstraints = GridBagConstraints()

		targetModulePanel.apply {
			layout = gridBagLayout
		}

		gridBagConstraints.gridx = 0
		gridBagConstraints.gridy = 0
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
		targetModulePanel.add(targetModuleLabel, gridBagConstraints)

		gridBagConstraints.gridx = 1
		gridBagConstraints.gridy = 0
		targetModulePanel.add(targetModuleButton, gridBagConstraints)

		gridBagConstraints.gridx = 0
		gridBagConstraints.gridy = 1
		targetModulePanel.add(newStringNameLabel, gridBagConstraints)

		gridBagConstraints.gridx = 1
		gridBagConstraints.gridy = 1
		targetModulePanel.add(newStringNameInput, gridBagConstraints)

		gridBagConstraints.gridx = 0
		gridBagConstraints.gridy = 2
		targetModulePanel.add(newStringValueRuLabel, gridBagConstraints)

		gridBagConstraints.gridx = 1
		gridBagConstraints.gridy = 2
		targetModulePanel.add(newStringValueRuInput, gridBagConstraints)

		gridBagConstraints.gridx = 0
		gridBagConstraints.gridy = 3
		targetModulePanel.add(newStringValueEnLabel, gridBagConstraints)

		gridBagConstraints.gridx = 1
		gridBagConstraints.gridy = 3
		targetModulePanel.add(newStringValueEnInput, gridBagConstraints)

		dialogPanel.apply {
			preferredWidth = 400
			add(targetModulePanel, BorderLayout.WEST)
		}

		return dialogPanel
	}

	override fun doOKAction() {
		if (targetModule != null) {
			try {
				super.doOKAction()
			} catch (e: Exception) {
				// stay tuned
			}
		}
	}

	override fun showTargetModuleSelector(modules: List<Module>) {
		SearchableListDialog(
			parent = dialogPanel,
			label = "Search modules",
			items = modules,
			searchBy = { it.name },
			itemSelectionListener = presenter::selectModule,
			itemRenderer = ModuleListRenderer()
		).show()
	}

	override fun changeTargetModuleButton(text: String, state: ButtonState) {
		targetModuleButton.changeModuleButton(text, state)
	}
}