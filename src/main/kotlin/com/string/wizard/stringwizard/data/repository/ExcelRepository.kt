package com.string.wizard.stringwizard.data.repository

import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.data.entity.Locale
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

class ExcelRepository {

	fun getStringsByLocale(file: File, locale: Locale): List<ExcelString> {
		val result = mutableListOf<ExcelString>()
		val workbook = WorkbookFactory.create(file)
		val sheet = workbook.getSheetAt(0)

		val localeRow = sheet.getRow(0)
		val localeCell = localeRow.find { it.stringCellValue.equals(locale.name, ignoreCase = true) } ?: error("Invalid file content")

		var rowIterator = 1
		while (true) {
			val cellValue = try {
				sheet.getRow(rowIterator).getCell(localeCell.address.column).stringCellValue
			} catch (e: Exception) {
				break
			}

			if (cellValue.isNotBlank()) {
				result.add(ExcelString(cellValue, rowIterator, locale))
				rowIterator++
			} else {
				break
			}
		}

		return result
	}
}