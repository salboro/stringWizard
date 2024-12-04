package com.string.wizard.stringwizard.ui.addfromexcel

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
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
import com.intellij.util.ui.JBUI.Borders
import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.presentation.addfromexcel.StringAddFromExcelPresenter
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.changeModuleButton
import com.string.wizard.stringwizard.ui.component.ExcelStringListRenderer
import com.string.wizard.stringwizard.ui.component.ModuleListRenderer
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_BORDER
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_HEIGHT
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_WIDTH
import com.string.wizard.stringwizard.ui.util.adjustColumnWidths
import org.jdesktop.swingx.HorizontalLayout
import org.jdesktop.swingx.VerticalLayout
import java.awt.BorderLayout
import java.awt.Dimension
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

		const val EXCEL_FILE_EXTENSION = "xlsx"

		const val STRING_SELECTOR_TITLE = "title"

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

	private val excelStringsTableColumns = arrayOf("Locale", "Copy from default", "Value")
	private val tableDataModel = object : DefaultTableModel() {
		override fun isCellEditable(row: Int, column: Int): Boolean {
			return false
		}
	}
	private val excelStringsTable = JBTable(tableDataModel)
	private val excelTablePanel = JBScrollPane(excelStringsTable)

	private val debugText = JBLabel()

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
			add(selectorLabel)
			add(fileSelector)
		}

		excelStringPanel.apply {
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
			add(chosenTargetModuleLabel)
			add(targetModuleButton)
		}

		tableDataModel.setColumnIdentifiers(excelStringsTableColumns)

		excelStringsTable.apply {
			isStriped = true
			gridColor = JBColor.GRAY
			intercellSpacing = Dimension(1, 1)
			autoResizeMode = JBTable.AUTO_RESIZE_OFF
		}

		excelTablePanel.apply {
			border = Borders.empty(MAIN_BORDER, 0)
			horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
			isVisible = false
			preferredSize = Dimension(1000, 300)
		}

		mainPanel.apply {
			add(fileSelectorPanel)
			add(excelStringPanel)
			add(targetModulePanel)
			add(excelTablePanel)
			add(debugText)
		}

		dialogPanel.apply {
			preferredSize = Dimension(MAIN_DIALOG_WIDTH, MAIN_DIALOG_HEIGHT)

			add(mainPanel, BorderLayout.CENTER)
		}

		return dialogPanel
	}

	override fun showDebugText(text: String) {
		debugText.text = text
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
	}

	override fun hideExcelStrings() {
		excelTablePanel.isVisible = false
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
}