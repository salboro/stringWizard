package com.string.wizard.stringwizard.presentation.addfromexcel

import com.intellij.openapi.vfs.VirtualFile
import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.repository.ExcelRepository
import com.string.wizard.stringwizard.ui.addfromexcel.StringAddFromExcelUi
import com.string.wizard.stringwizard.ui.util.formatExcelString
import java.io.File

class StringAddFromExcelPresenter(private val ui: StringAddFromExcelUi) {

	private val excelRepository = ExcelRepository()

	private var excelFile: File? = null
	private var excelString: ExcelString? = null

	fun chooseFile(file: VirtualFile) {
		excelFile = File(file.path)

		ui.enableExcelString(true)
		ui.changeExcelString("")
	}

	fun onExcelStringSelectClick() {
		try {
			val excelFile = excelFile ?: return
			val strings = excelRepository.getStringsByLocale(excelFile, Locale.RU)

			ui.showStringSelector(strings)
		} catch (e: Exception) {
			ui.showDebugText(e.message ?: "aboba")
		}
	}

	fun selectExcelString(string: ExcelString) {
		excelString = string
		ui.changeExcelString(formatExcelString(string.value, string.position, string.locale))
	}
}