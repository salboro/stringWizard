package com.string.wizard.stringwizard.ui.addfromexcel

import com.string.wizard.stringwizard.data.entity.ExcelString

interface StringAddFromExcelUi {

	fun showDebugText(text: String)

	fun enableExcelString(enabled: Boolean)

	fun showStringSelector(strings: List<ExcelString>)

	fun changeExcelString(string: String)

	fun showExcelStrings(strings: List<ExcelString>)

	fun hideExcelStrings()
}