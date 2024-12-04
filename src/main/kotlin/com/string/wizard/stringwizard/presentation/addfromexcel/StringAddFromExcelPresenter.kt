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
	private var excelStrings: List<ExcelString>? = null

	fun chooseFile(file: VirtualFile) {
		excelFile = File(file.path)

		ui.enableExcelString(true)
		ui.changeExcelString("")
		ui.hideExcelStrings()
	}

	fun onExcelStringSelectClick() {
		try {
			val excelFile = excelFile ?: return
			val strings = excelRepository.getStringsByLocale(excelFile, Locale.RU)

			ui.hideExcelStrings()
			ui.showStringSelector(strings)
		} catch (e: Exception) {
			ui.showDebugText(e.message ?: "aboba")
			ui.hideExcelStrings()
		}
	}

	fun selectExcelString(string: ExcelString) {
		val excelFile = excelFile ?: return
		val stringsForAllLocales = excelRepository.getStringsForAllLocale(excelFile, string)
		excelStrings = stringsForAllLocales
		ui.changeExcelString(formatExcelString(string.value, string.position, string.locale))
		ui.showExcelStrings(stringsForAllLocales)
	}
}