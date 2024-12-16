package com.string.wizard.stringwizard.ui.stringcopy

import com.intellij.collaboration.ui.CollaborationToolsUIUtil.defaultButton
import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI.Borders
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.presentation.stringcopy.StringCopyDialogPresenter
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.changeModuleButton
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import com.string.wizard.stringwizard.ui.renderer.ModuleListRenderer
import com.string.wizard.stringwizard.ui.renderer.StringListRenderer
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_BORDER
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_HEIGHT
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_WIDTH
import com.string.wizard.stringwizard.ui.util.copySuccessMessageFormat
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

		const val SOURCE_TITLE = "Source"
		const val SOURCE_MODULE_LABEL = "Chosen module: "
		const val SOURCE_STRING_LABEL = "Chosen string from resources: "

		const val TARGET_TITLE = "Target"
		const val TARGET_MODULE_LABEL = "Chosen module: "
		const val NEW_STRING_DEFAULT_TEXT = "Choose source string first!"
		const val NEW_STRING_LABEL = "Input new string name: "
		const val DOMAIN_LABEL = "Select domain: "
		const val CREATE_FILE_LABEL = "Create file by selected domain "
		const val CREATE_FILE_BUTTON = "Create file"

		const val CANCEL = "Cancel"
		const val OK = "OK"
		const val COPY = "Copy"
		const val ATTENTION_DEFAULT_TEXT = "Fill all fields before copy! Also recommend sync (if you haven't done it yet) and commit/stash changes"

		const val STRING_SELECTOR_TITLE = "Select string from resources"
		const val MODULE_SELECTOR_TITLE = "Search modules"

		const val UNKNOWN_ERROR = "Unknown error"
	}

	private val presenter = StringCopyDialogPresenter(this, project)

	private val dialogPanel = DialogPanel(BorderLayout())
	private val mainPanel = JPanel(VerticalLayout())

	private val sourceLabel = JBLabel(SOURCE_TITLE)
	private val targetLabel = JBLabel(TARGET_TITLE)

	private val sourceModulePanel = JPanel(HorizontalLayout())
	private val chosenSourceModuleLabel = JBLabel(SOURCE_MODULE_LABEL)
	private val sourceModuleButton = JButton("", AllIcons.General.Add)

	private val sourceStringPanel = JPanel(HorizontalLayout())
	private val chosenSourceStringLabel = JBLabel(SOURCE_STRING_LABEL)
	private val sourceStringButton = JButton("", AllIcons.General.Add)

	private val stringSelectionError = JBLabel()

	private val targetModulePanel = JPanel(HorizontalLayout())
	private val chosenTargetModuleLabel = JBLabel(TARGET_MODULE_LABEL)
	private val targetModuleButton = JButton("", AllIcons.General.Add)

	private val newStringPanel = JPanel(HorizontalLayout())
	private val newStringLabel = JBLabel(NEW_STRING_LABEL)
	private val newStringInput = JBTextField(NEW_STRING_DEFAULT_TEXT, 50)

	private val domainPanel = JPanel(HorizontalLayout())
	private val domainLabel = JBLabel(DOMAIN_LABEL)
	private val domainList = ComboBox(Domain.values())

	private val attentionText = JBLabel(ATTENTION_DEFAULT_TEXT)

	private val createFilesPanel = JPanel(HorizontalLayout())
	private val createFilesLabel = JBLabel(CREATE_FILE_LABEL)
	private val createFilesButton = JButton(CREATE_FILE_BUTTON)

	private val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
	private val cancelButton = JButton(CANCEL)
	private val okButton = JButton(OK).defaultButton()
	private val copyButton = JButton(COPY).defaultButton()

	init {
		title = TITLE
		init()
	}

	override fun createCenterPanel(): JComponent {
		sourceLabel.apply {
			border = Borders.empty(MAIN_BORDER)
			horizontalAlignment = JBLabel.CENTER
			font = JBFont.h4()
		}

		targetLabel.apply {
			border = Borders.empty(MAIN_BORDER)
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
			border = Borders.emptyBottom(MAIN_BORDER)
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
			border = Borders.emptyBottom(MAIN_BORDER)
			add(newStringLabel)
			add(newStringInput)
		}

		domainList.apply {
			selectedItem = Domain.DP
			isEditable = false
			addActionListener { (selectedItem as? Domain)?.let(presenter::selectDomain) }
		}

		domainPanel.apply {
			border = Borders.empty(MAIN_BORDER, 0)
			add(domainLabel)
			add(domainList)
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
			border = Borders.emptyTop(MAIN_BORDER)
			foreground = JBColor.RED
			font = JBFont.regular().asBold()
		}

		createFilesButton.addActionListener { presenter.createStringFiles() }

		createFilesPanel.apply {
			isVisible = false
			border = Borders.emptyBottom(MAIN_BORDER)
			add(createFilesLabel)
			add(createFilesButton)
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
			add(domainPanel)
			add(attentionText)
			add(createFilesPanel)
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
			label = STRING_SELECTOR_TITLE,
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
			label = MODULE_SELECTOR_TITLE,
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
			text = copySuccessMessageFormat(sourceModuleName, targetModuleName, sourceStringName, newStringName)
		}
	}

	override fun showCopyFailed(exception: Exception) {
		attentionText.apply {
			foreground = JBColor.RED
			text = exception.message ?: UNKNOWN_ERROR
		}
	}

	override fun showStringSelectionFailed(exception: Exception) {
		stringSelectionError.apply {
			isVisible = true
			foreground = JBColor.RED
			text = exception.message ?: UNKNOWN_ERROR
		}
	}

	override fun hideStringSelectionFailed() {
		stringSelectionError.isVisible = false
	}

	override fun showSuccessCreateFiles() {
		attentionText.apply {
			foreground = JBColor.GREEN
			text = "File created successfully!"
		}
	}

	override fun setCreateFilesButtonVisible(visible: Boolean) {
		createFilesPanel.isVisible = visible
	}
}