package com.string.wizard.stringwizard.ui.stringcopy

import com.intellij.collaboration.ui.CollaborationToolsUIUtil.defaultButton
import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI.Borders
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.changeModuleButton
import com.string.wizard.stringwizard.ui.component.ModuleListRenderer
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import com.string.wizard.stringwizard.ui.component.StringListRenderer
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_HEIGHT
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_WIDTH
import org.jdesktop.swingx.HorizontalLayout
import org.jdesktop.swingx.VerticalLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSeparator

class StringCopyDialog(
	project: Project
) : DialogWrapper(
	project,
	null,
	false,
	IdeModalityType.IDE,
	false
), StringCopyDialogUi {

	private companion object {

		const val TITLE = "Copy string"
		const val BORDER = 10
		const val NEW_STRING_DEFAULT_TEXT = "Choose source string first!"
	}

	private val presenter = StringCopyDialogPresenter(this, project)

	private val dialogPanel = DialogPanel(BorderLayout())
	private val mainPanel = JPanel(VerticalLayout())

	private val sourceLabel = JBLabel("Source")
	private val targetLabel = JBLabel("Target")

	private val sourceModulePanel = JPanel(HorizontalLayout())
	private val chosenSourceModuleLabel = JBLabel("Chosen module: ")
	private val sourceModuleButton = JButton("", AllIcons.General.Add)

	private val sourceStringPanel = JPanel(HorizontalLayout())
	private val chosenSourceStringLabel = JBLabel("Chosen string from resources: ")
	private val sourceStringButton = JButton("", AllIcons.General.Add)

	private val stringSelectionError = JBLabel()

	private val targetModulePanel = JPanel(HorizontalLayout())
	private val chosenTargetModuleLabel = JBLabel("Chosen module: ")
	private val targetModuleButton = JButton("", AllIcons.General.Add)

	private val newStringPanel = JPanel(HorizontalLayout())
	private val newStringLabel = JBLabel("Input new string name: ")
	private val newStringInput = JBTextField(NEW_STRING_DEFAULT_TEXT, 50)

	private val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
	private val cancelButton = JButton("Cancel")
	private val okButton = JButton("OK").defaultButton()
	private val copyButton = JButton("Copy").defaultButton()
	private val attentionText = JBLabel("Fill all fields before copy! Also recommend sync (if you haven't done it yet) and commit/stash changes")

	init {
		title = TITLE
		init()
	}

	override fun createCenterPanel(): JComponent {
		sourceLabel.apply {
			border = Borders.empty(BORDER)
			horizontalAlignment = JBLabel.CENTER
			font = JBFont.h4()
		}

		targetLabel.apply {
			border = Borders.empty(BORDER)
			horizontalAlignment = JBLabel.CENTER
			font = JBFont.h4()
		}

		sourceModulePanel.apply {
			add(chosenSourceModuleLabel)
			add(sourceModuleButton)
		}

		sourceStringPanel.apply {
			add(chosenSourceStringLabel)
			add(sourceStringButton)
			border = Borders.emptyBottom(BORDER)
		}

		sourceModuleButton.addActionListener {
			presenter.onModulesChooserClick()
		}

		sourceStringButton.addActionListener {
			presenter.onStringSelectionClick()
		}

		stringSelectionError.apply {
			isVisible = false
			foreground = JBColor.RED
			font = JBFont.regular().asBold()
		}

		targetModulePanel.apply {
			add(chosenTargetModuleLabel)
			add(targetModuleButton)
		}

		targetModuleButton.addActionListener {
			presenter.onTargetModuleSelectorClick()
		}

		newStringInput.apply {
			isEnabled = false
		}

		newStringPanel.apply {
			border = Borders.emptyBottom(BORDER)
			add(newStringLabel)
			add(newStringInput)
		}

		copyButton.apply {
			isEnabled = false
			addActionListener { presenter.copy(newStringInput.text) }
		}

		cancelButton.apply {
			addActionListener { super.doCancelAction() }
		}

		okButton.apply {
			addActionListener { super.doOKAction() }
		}

		attentionText.apply {
			border = Borders.emptyTop(BORDER)
			foreground = JBColor.RED
			font = JBFont.regular().asBold()
		}

		buttonsPanel.apply {
			add(cancelButton)
			add(copyButton)
			add(okButton)
		}

		mainPanel.apply {
			add(sourceLabel)
			add(sourceModulePanel)
			add(sourceStringPanel)
			add(stringSelectionError)
			add(JSeparator())
			add(targetLabel)
			add(targetModulePanel)
			add(newStringPanel)
			add(JSeparator())
			add(attentionText)
		}

		dialogPanel.apply {
			preferredSize = Dimension(MAIN_DIALOG_WIDTH, MAIN_DIALOG_HEIGHT)

			add(mainPanel, BorderLayout.CENTER)
			add(buttonsPanel, BorderLayout.SOUTH)
		}

		return dialogPanel
	}

	override fun showSourceModulesSelector(modules: List<Module>) {
		createModuleSearchDialog(modules, presenter::selectModule).show()
	}

	override fun changeSourceModuleButton(text: String, state: ButtonState) {
		sourceModuleButton.changeModuleButton(text, state)
	}

	override fun showSourceStringSelector(strings: List<ResourceString>) {
		createStringSelectorDialog(strings).show()
	}

	private fun createStringSelectorDialog(strings: List<ResourceString>): SearchableListDialog<ResourceString> =
		SearchableListDialog(
			parent = dialogPanel,
			label = "Select string from resources",
			items = strings,
			searchBy = { it.name },
			itemSelectionListener = presenter::selectString,
			itemRenderer = StringListRenderer(),
		)

	override fun changeSourceStringButton(text: String, state: ButtonState) {
		sourceStringButton.text = text
		sourceStringButton.icon = when (state) {
			ButtonState.EMPTY  -> AllIcons.General.Add
			ButtonState.FILLED -> AllIcons.FileTypes.Xml
		}
	}

	override fun showTargetModuleSelector(modules: List<Module>) {
		createModuleSearchDialog(modules, presenter::selectTargetModule).show()
	}

	private fun createModuleSearchDialog(modules: List<Module>, selectionListener: (Module) -> Unit): SearchableListDialog<Module> =
		SearchableListDialog(
			parent = dialogPanel,
			label = "Search modules",
			items = modules,
			searchBy = { it.name },
			itemSelectionListener = selectionListener,
			itemRenderer = ModuleListRenderer()
		)

	override fun changeTargetModuleButton(text: String, state: ButtonState) {
		targetModuleButton.changeModuleButton(text, state)
	}

	override fun changeNewStringName(text: String) {
		newStringInput.apply {
			isEnabled = true
			this.text = text
		}
	}

	override fun disableNewStringName() {
		newStringInput.apply {
			isEnabled = false
			text = NEW_STRING_DEFAULT_TEXT
		}
	}

	override fun changeCopyButtonEnabled(enabled: Boolean) {
		copyButton.isEnabled = enabled
	}

	override fun showSuccess(sourceModuleName: String, targetModuleName: String, sourceStringName: String, newStringName: String) {
		attentionText.apply {
			foreground = JBColor.GREEN
			text = createSuccessMessage(sourceModuleName, targetModuleName, sourceStringName, newStringName)
		}
	}

	private fun createSuccessMessage(sourceModuleName: String, targetModuleName: String, sourceStringName: String, newStringName: String): String =
		"<html>String <b><font color=#7EDC95>$sourceStringName</b></font> successfully copied from module <b><font color=#7EDC95>$sourceModuleName</b></font>" +
			" into module <b><font color=#7EDC95>$targetModuleName</b></font> with name <b><font color=#7EDC95>$newStringName</b></font><html>"

	override fun showCopyFailed(exception: Exception) {
		attentionText.apply {
			foreground = JBColor.RED
			text = exception.message ?: "Unknown error"
		}
	}

	override fun showStringSelectionFailed(exception: Exception) {
		stringSelectionError.apply {
			isVisible = true
			foreground = JBColor.RED
			text = exception.message ?: "Unknown error"
		}
	}

	override fun hideStringSelectionFailed() {
		stringSelectionError.isVisible = false
	}
}