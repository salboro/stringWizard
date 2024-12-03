package com.string.wizard.stringwizard.ui.addfromexcel

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.string.wizard.stringwizard.presentation.addfromexcel.StringAddFromExcelPresenter
import javax.swing.JComponent

class StringAddFromExcelDialog(
	project: Project
) : DialogWrapper(
	project,
	null,
	false,
	IdeModalityType.IDE,
	false
), StringAddFromExcelUi {

	private val presenter = StringAddFromExcelPresenter(this)

	override fun createCenterPanel(): JComponent? {
		TODO("Not yet implemented")
	}

}