package com.string.wizard.stringwizard.ui.stringsort

import com.intellij.collaboration.ui.CollaborationToolsUIUtil.defaultButton
import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.string.wizard.stringwizard.presentation.stringsort.StringSortPresenter
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.addfromexcel.AttentionTextState
import com.string.wizard.stringwizard.ui.changeButton
import com.string.wizard.stringwizard.ui.component.ButtonWithLabel
import com.string.wizard.stringwizard.ui.component.ModuleListRenderer
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_HEIGHT
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_WIDTH
import com.string.wizard.stringwizard.ui.resources.Strings
import org.jdesktop.swingx.VerticalLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class StringSortDialog(private val project: Project, dialogTitle: String) : DialogWrapper(
	project,
	null,
	false,
	IdeModalityType.IDE,
	false
), StringSortDialogUi {

	private val presenter = StringSortPresenter(this, project)
	private val dialogPanel = DialogPanel(BorderLayout())
	private val mainPanel = JPanel(VerticalLayout())
	private val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))

	private val cancelButton = JButton("Cancel")
	private val okButton = JButton("Ok").defaultButton()
	private val sortButton = JButton("Sort").defaultButton()
	private val attentionText = JBLabel()

	private val targetModuleView = ButtonWithLabel(Strings.TARGET_MODULE, Strings.CHOOSE_MODULE, AllIcons.General.Add)

	init {
		title = dialogTitle
		init()
	}

	override fun createCenterPanel(): JComponent {
		setActionListeners()
		buttonsPanel.apply {
			add(cancelButton)
			add(sortButton)
			add(okButton)
		}

		attentionText.isVisible = false

		mainPanel.apply {
			add(targetModuleView)
			add(attentionText)
		}

		dialogPanel.apply {
			preferredSize = Dimension(MAIN_DIALOG_WIDTH, MAIN_DIALOG_HEIGHT)
			add(mainPanel, BorderLayout.CENTER)
			add(buttonsPanel, BorderLayout.SOUTH)
		}

		return dialogPanel
	}

	private fun setActionListeners() {
		targetModuleView.setActionListener(presenter::onTargetModuleSelectorClick)
		cancelButton.addActionListener { super.doCancelAction() }
		okButton.addActionListener { super.doOKAction() }
		sortButton.addActionListener { presenter.sortString() }
	}

	override fun setAttentionText(text: String, state: AttentionTextState) {
		attentionText.apply {
			isVisible = true
			this.text = text
			foreground = when (state) {
				AttentionTextState.ERROR   -> JBColor.RED
				AttentionTextState.SUCCESS -> JBColor.GREEN
			}
		}
	}

	override fun showTargetModuleSelector(modules: List<Module>) {
		SearchableListDialog(
			parent = dialogPanel,
			label = Strings.SEARCH_MODULE,
			items = modules,
			searchBy = { it.name },
			itemSelectionListener = { module ->
				presenter.changeModule(module)
				targetModuleView.changeButton(module.name, ButtonState.FILLED)
			},
			itemRenderer = ModuleListRenderer()
		).show()
	}

	override fun hideAttentionText() {
		attentionText.apply {
			isVisible = false
			text = ""
		}
	}
}