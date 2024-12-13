package com.string.wizard.stringwizard.ui.stringadd

import com.intellij.collaboration.ui.CollaborationToolsUIUtil.defaultButton
import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI.Borders
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.domain.entity.NewString
import com.string.wizard.stringwizard.presentation.stringadd.StringAddPresenter
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.changeButton
import com.string.wizard.stringwizard.ui.component.ButtonWithLabel
import com.string.wizard.stringwizard.ui.component.ModuleListRenderer
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_BORDER
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_HEIGHT
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_WIDTH
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

class StringAddDialog(project: Project, dialogTitle: String) : DialogWrapper(
	project,
	null,
	false,
	IdeModalityType.IDE,
	false
), StringAddDialogUi {

	private val presenter = StringAddPresenter(this, project)
	private val dialogPanel = DialogPanel(BorderLayout())
	private val mainPanel = JPanel(VerticalLayout())
	private val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))

	private val cancelButton = JButton("Cancel")
	private val okButton = JButton("Ok").defaultButton()
	private val addButton = JButton("Add").defaultButton()

	private val targetModuleView = ButtonWithLabel(labelText = "Target module:", "Choose Module", AllIcons.General.Add)
	private val createFilesView = ButtonWithLabel(labelText = "Create file:", text = "Create", AllIcons.Actions.AddFile)

	private val newStringNameRow = JPanel(HorizontalLayout())
	private val newStringNameLabel = JBLabel("Enter string name:")
	private val newStringInput = JBTextField()

	private val domainRow = JPanel(HorizontalLayout())
	private val domainLabel = JBLabel("Select domain: ")
	private val domainList = ComboBox(Domain.values())

	private val tableLabel = JBLabel("Add string values")
	private val stringsTableColumns = arrayOf("Locale", "Value")
	private val tableDataModel = getTableModel()
	private val newStringsTable = JBTable(tableDataModel)
	private val tablePanel = JBScrollPane(newStringsTable)

	private val attentionText = JBLabel()

	private fun getTableModel(): DefaultTableModel =
		object : DefaultTableModel() {
			override fun isCellEditable(row: Int, column: Int): Boolean =
				column == 1

			override fun setValueAt(aValue: Any?, row: Int, column: Int) {
				val newValue = (aValue as? String) ?: return
				presenter.changeStringValue(newValue, row)

				super.setValueAt(aValue, row, column)
			}
		}

	init {
		title = dialogTitle
		init()
	}

	override fun createCenterPanel(): JComponent {
		addListeners()

		buttonsPanel.apply {
			add(cancelButton)
			add(addButton)
			add(okButton)
		}

		domainList.apply {
			selectedItem = Domain.DP
			isEditable = false
			addActionListener { (selectedItem as? Domain)?.let(presenter::selectDomain) }
		}

		domainRow.apply {
			add(domainLabel)
			add(domainList)
		}

		newStringNameRow.apply {
			add(newStringNameLabel)
			add(newStringInput)
		}

		createFilesView.isVisible = false

		tableLabel.apply {
			isVisible = false
			border = Borders.emptyTop(MAIN_BORDER)
		}

		tableDataModel.setColumnIdentifiers(stringsTableColumns)

		newStringsTable.apply {
			isStriped = true
			gridColor = JBColor.GRAY
			intercellSpacing = Dimension(1, 1)
			autoResizeMode = JBTable.AUTO_RESIZE_OFF
		}

		tablePanel.apply {
			border = Borders.emptyBottom(MAIN_BORDER)
			horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
			isVisible = false
			preferredSize = Dimension(1000, 300)
		}

		mainPanel.apply {
			add(targetModuleView)
			add(domainRow)
			add(newStringNameRow)
			add(tableLabel)
			add(tablePanel)
			add(createFilesView)
			add(attentionText)
		}

		dialogPanel.apply {
			preferredSize = Dimension(MAIN_DIALOG_WIDTH, MAIN_DIALOG_HEIGHT)
			add(mainPanel, BorderLayout.CENTER)
			add(buttonsPanel, BorderLayout.SOUTH)
		}

		return dialogPanel
	}

	private fun addListeners() {
		targetModuleView.setActionListener(presenter::onTargetModuleSelectorClick)
		createFilesView.setActionListener(presenter::createFiles)
		addButton.addActionListener { presenter.onAddButtonClick(newStringInput.text) }
		cancelButton.addActionListener { super.doCancelAction() }
		okButton.addActionListener { super.doOKAction() }
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
		targetModuleView.changeButton(text, state)
	}

	override fun showNewStrings(strings: List<NewString>) {
		tableDataModel.rowCount = 0

		strings.forEach {
			tableDataModel.addRow(arrayOf(it.locale.name, it.value))
		}

		newStringsTable.adjustColumnWidths()

		tableLabel.isVisible = true
		tablePanel.isVisible = true
	}

	override fun setCreateFilesVisible(visible: Boolean) {
		createFilesView.isVisible = visible
	}

	override fun setAttentionText(text: String, state: BottomTextState) {
		attentionText.apply {
			isVisible = true
			this.text = text
			foreground = when (state) {
				BottomTextState.ERROR   -> JBColor.RED
				BottomTextState.SUCCESS -> JBColor.GREEN
			}
		}
	}

	override fun setNewStringName(moduleName: String) {
		newStringInput.apply {
			isEnabled = true
			this.text = formatModuleName(moduleName).replace("-", "_")
		}
	}
}