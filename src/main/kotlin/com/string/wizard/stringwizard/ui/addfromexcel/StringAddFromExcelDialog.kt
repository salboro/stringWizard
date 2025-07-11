package com.string.wizard.stringwizard.ui.addfromexcel

import com.intellij.collaboration.ui.CollaborationToolsUIUtil.defaultButton
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI.Borders
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.presentation.addfromexcel.StringAddFromExcelPresenter
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.changeModuleButton
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import com.string.wizard.stringwizard.ui.renderer.ExcelStringListRenderer
import com.string.wizard.stringwizard.ui.renderer.ModuleListRenderer
import com.string.wizard.stringwizard.ui.renderer.UnicodeTableCellRenderer
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_BORDER
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_HEIGHT
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_WIDTH
import com.string.wizard.stringwizard.ui.resources.Dimension.SMALL_BORED
import com.string.wizard.stringwizard.ui.resources.Strings.UNICODE_SYMBOLS_ATTENTION_TEXT
import com.string.wizard.stringwizard.ui.util.adjustColumnWidths
import com.string.wizard.stringwizard.ui.util.formatModuleName
import org.jdesktop.swingx.HorizontalLayout
import org.jdesktop.swingx.VerticalLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

class StringAddFromExcelDialog(
	project: Project
) : DialogWrapper(
	project,
	null,
	false,
	IdeModalityType.IDE,
	false
), StringAddFromExcelUi {

	private companion object {

		const val TITLE = "Add string from Excel"

		const val FILE_SELECTOR_LABEL = "Choose excel source file"
		const val EXCEL_STRING_LABEL = "Choose excel string"
		const val TARGET_MODULE_LABEL = "Chosen module: "
		const val MODULE_SELECTOR_TITLE = "Search modules"
		const val NEW_STRING_DEFAULT_TEXT = "Choose source string first!"
		const val NEW_STRING_LABEL = "Input new string name: "
		const val TABLE_LABEL = "Excel string values: "
		const val DOMAIN_LABEL = "Select domain: "
		const val CREATE_FILE_LABEL = "Create file by selected domain "
		const val CREATE_FILE_BUTTON = "Create file"

		const val EXCEL_FILE_EXTENSION = "xlsx"

		const val STRING_SELECTOR_TITLE = "title"

		const val CANCEL = "Cancel"
		const val OK = "OK"
		const val ADD = "Add"

		const val MAX_TEXT_LENGTH = 150
	}

	private val presenter = StringAddFromExcelPresenter(this, project)

	private val dialogPanel = DialogPanel(BorderLayout())
	private val mainPanel = JPanel(VerticalLayout())

	private val fileSelectorPanel = JPanel(HorizontalLayout())

	private val selectorLabel = JBLabel(FILE_SELECTOR_LABEL)

	private val fileDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(EXCEL_FILE_EXTENSION)
	private val fileTextField = JBTextField("Select excel file", 50)
	private val fileSelector = TextFieldWithBrowseButton(fileTextField)

	private val excelStringPanel = JPanel(HorizontalLayout())
	private val excelStringLabel = JBLabel(EXCEL_STRING_LABEL)
	private val excelStringButton = JButton("", AllIcons.General.Add)

	private val targetModulePanel = JPanel(HorizontalLayout())
	private val chosenTargetModuleLabel = JBLabel(TARGET_MODULE_LABEL)
	private val targetModuleButton = JButton("", AllIcons.General.Add)

	private val newStringPanel = JPanel(HorizontalLayout())
	private val newStringLabel = JBLabel(NEW_STRING_LABEL)
	private val newStringInput = JBTextField(NEW_STRING_DEFAULT_TEXT, 50)

	private val domainPanel = JPanel(HorizontalLayout())
	private val domainLabel = JBLabel(DOMAIN_LABEL)
	private val domainList = ComboBox(Domain.values())

	private val tableLabel = JBLabel(TABLE_LABEL)
	private val tableAttentionText = JBLabel(UNICODE_SYMBOLS_ATTENTION_TEXT)
	private val excelStringsTableColumns = arrayOf("Locale", "Copy from default", "Value")
	private val tableDataModel = getTableModel()
	private val excelStringsTable = JBTable(tableDataModel)
	private val excelTablePanel = JBScrollPane(excelStringsTable)

	private val attentionText = JBLabel()

	private val createFilesPanel = JPanel(HorizontalLayout())
	private val createFilesLabel = JBLabel(CREATE_FILE_LABEL)
	private val createFilesButton = JButton(CREATE_FILE_BUTTON)

	private val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
	private val cancelButton = JButton(CANCEL)
	private val okButton = JButton(OK).defaultButton()
	private val addButton = JButton(ADD).defaultButton()

	private val browseListener = object : TextBrowseFolderListener(fileDescriptor) {
		override fun onFileChosen(chosenFile: VirtualFile) {
			super.onFileChosen(chosenFile)

			presenter.chooseFile(chosenFile)
		}
	}

	init {
		title = TITLE
		init()
	}

	override fun createCenterPanel(): JComponent {
		fileSelector.addBrowseFolderListener(browseListener)
		fileTextField.isEditable = false

		fileSelectorPanel.apply {
			border = Borders.emptyBottom(MAIN_BORDER)
			add(selectorLabel)
			add(fileSelector)
		}

		excelStringPanel.apply {
			border = Borders.emptyBottom(MAIN_BORDER)
			add(excelStringLabel)
			add(excelStringButton)
		}

		excelStringButton.apply {
			isEnabled = false

			addActionListener { presenter.onExcelStringSelectClick() }
		}

		targetModuleButton.addActionListener {
			presenter.onTargetModuleSelectorClick()
		}

		targetModulePanel.apply {
			border = Borders.emptyBottom(MAIN_BORDER)
			add(chosenTargetModuleLabel)
			add(targetModuleButton)
		}

		createFilesButton.addActionListener { presenter.createStringFiles() }

		createFilesPanel.apply {
			isVisible = false
			border = Borders.emptyBottom(MAIN_BORDER)
			add(createFilesLabel)
			add(createFilesButton)
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
			border = Borders.emptyBottom(MAIN_BORDER)
			add(domainLabel)
			add(domainList)
		}

		tableLabel.apply {
			isVisible = false
			border = Borders.emptyTop(MAIN_BORDER)
			border = Borders.emptyBottom(SMALL_BORED)
		}

		tableDataModel.setColumnIdentifiers(excelStringsTableColumns)

		excelStringsTable.apply {
			isStriped = true
			gridColor = JBColor.GRAY
			intercellSpacing = Dimension(1, 1)
			autoResizeMode = JBTable.AUTO_RESIZE_OFF
			setDefaultRenderer(Any::class.java, UnicodeTableCellRenderer())
		}

		excelTablePanel.apply {
			border = Borders.emptyBottom(MAIN_BORDER)
			horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
			isVisible = false
			preferredSize = Dimension(1000, 300)
		}

		tableAttentionText.apply {
			isVisible = false
			border = Borders.empty(SMALL_BORED, 0)
			font = JBFont.regular().asBold()
			foreground = JBColor.ORANGE
		}

		addButton.apply {
			addActionListener { presenter.add(newStringInput.text) }
		}

		cancelButton.apply {
			addActionListener { super.doCancelAction() }
		}

		okButton.apply {
			addActionListener { super.doOKAction() }
		}

		attentionText.apply {
			border = Borders.emptyBottom(MAIN_BORDER)
		}

		buttonsPanel.apply {
			add(cancelButton)
			add(addButton)
			add(okButton)
		}

		mainPanel.apply {
			add(fileSelectorPanel)
			add(excelStringPanel)
			add(targetModulePanel)
			add(newStringPanel)
			add(domainPanel)
			add(tableLabel)
			add(excelTablePanel)
			add(tableAttentionText)
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

	override fun setAttentionText(text: String, state: AttentionTextState) {
		attentionText.apply {
			this.text = text
			foreground = when (state) {
				AttentionTextState.ERROR   -> JBColor.RED
				AttentionTextState.SUCCESS -> JBColor.GREEN
			}
		}
	}

	override fun enableExcelString(enabled: Boolean) {
		excelStringButton.isEnabled = enabled
	}

	override fun showStringSelector(strings: List<ExcelString>) {
		SearchableListDialog(
			parent = dialogPanel,
			label = STRING_SELECTOR_TITLE,
			items = strings,
			searchBy = { it.value },
			itemSelectionListener = presenter::selectExcelString,
			itemRenderer = ExcelStringListRenderer(),
		).show()
	}

	override fun changeExcelString(string: String) {
		excelStringButton.apply {
			text = string.take(MAX_TEXT_LENGTH)
			icon = if (string.isNotBlank()) {
				AllIcons.FileTypes.XsdFile
			} else {
				AllIcons.General.Add
			}
		}
	}

	override fun showExcelStrings(strings: List<ExcelString>) {
		tableDataModel.rowCount = 0

		strings.forEach {
			tableDataModel.addRow(arrayOf(it.locale.name, it.fromDefault, it.value))
		}

		excelStringsTable.adjustColumnWidths()

		excelTablePanel.isVisible = true
		tableLabel.isVisible = true
		tableAttentionText.isVisible = true
	}

	override fun hideExcelStrings() {
		excelTablePanel.isVisible = false
		tableLabel.isVisible = false
	}

	override fun showTargetModuleSelector(modules: List<Module>) {
		SearchableListDialog(
			parent = dialogPanel,
			label = MODULE_SELECTOR_TITLE,
			items = modules,
			searchBy = { it.name },
			itemSelectionListener = presenter::selectTargetModule,
			itemRenderer = ModuleListRenderer()
		).show()
	}

	override fun changeTargetModuleButton(text: String, state: ButtonState) {
		targetModuleButton.changeModuleButton(text, state)
	}

	override fun changeNewStringName(text: String) {
		newStringInput.apply {
			isEnabled = true
			this.text = formatModuleName(text).replace("-", "_")
		}
	}

	override fun dispose() {
		presenter.onDispose()
		super.dispose()
	}

	private fun getTableModel(): DefaultTableModel =
		object : DefaultTableModel() {
			override fun isCellEditable(row: Int, column: Int): Boolean =
				column == 2

			override fun setValueAt(aValue: Any?, row: Int, column: Int) {
				val newValue = (aValue as? String) ?: return
				presenter.changeExcelStringValue(newValue, row)

				super.setValueAt(aValue, row, column)
			}
		}

	override fun setCreateFilesButtonVisible(visible: Boolean) {
		createFilesPanel.isVisible = visible
	}
}