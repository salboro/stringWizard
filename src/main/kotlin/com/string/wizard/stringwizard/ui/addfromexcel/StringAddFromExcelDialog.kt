package com.string.wizard.stringwizard.ui.addfromexcel

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.string.wizard.stringwizard.presentation.addfromexcel.StringAddFromExcelPresenter
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_HEIGHT
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_WIDTH
import org.jdesktop.swingx.HorizontalLayout
import org.jdesktop.swingx.VerticalLayout
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel

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

		const val EXCEL_FILE_EXTENSION = "xlsx"
	}

	private val presenter = StringAddFromExcelPresenter(this)

	private val dialogPanel = DialogPanel(BorderLayout())
	private val mainPanel = JPanel(VerticalLayout())

	private val fileSelectorPanel = JPanel(HorizontalLayout())

	private val selectorLabel = JBLabel(FILE_SELECTOR_LABEL)

	private val fileDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(EXCEL_FILE_EXTENSION)
	private val fileTextField = JBTextField("Select excel file", 50)
	private val fileSelector = TextFieldWithBrowseButton(fileTextField)

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

		mainPanel.apply {
			add(fileSelectorPanel)
		}

		dialogPanel.apply {
			preferredSize = Dimension(MAIN_DIALOG_WIDTH, MAIN_DIALOG_HEIGHT)

			add(mainPanel, BorderLayout.CENTER)
		}

		return dialogPanel
	}

}