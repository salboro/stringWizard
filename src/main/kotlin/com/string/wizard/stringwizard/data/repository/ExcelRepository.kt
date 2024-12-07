package com.string.wizard.stringwizard.data.repository

import com.intellij.openapi.util.io.FileUtil
import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.util.getDefaultLocale
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

class ExcelRepository {

	private var file: File? = null
	private var workbook: Workbook? = null

	fun getStrings(file: File, locale: Locale): List<ExcelString> {
		val result = mutableListOf<ExcelString>()
		val sheet = getSheet(file)

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

	fun getStrings(file: File, position: Int): List<ExcelString> {
		val result = mutableListOf<ExcelString>()
		val sheet = getSheet(file)

		val localeRow = sheet.getRow(0)
		val stringRow = sheet.getRow(position)

		for (cell in localeRow.toList()) {
			val localeValue = cell.stringCellValue
			val stringValue = stringRow.getCell(cell.address.column)?.stringCellValue.orEmpty()
			if (localeValue.isNotBlank() && stringValue.isNotBlank()) {
				result.add(ExcelString(stringValue, position, Locale.valueOf(localeValue)))
			} else {
				break
			}
		}

		return if (result.size < Locale.values().size) {
			result.addMissingValues()
		} else {
			result
		}
	}

	private fun List<ExcelString>.addMissingValues(): List<ExcelString> {
		val result = mutableListOf<ExcelString>()
		val foundLocales = map { it.locale }
		val missingLocales = Locale.values().filter { it !in foundLocales }

		missingLocales.forEach { locale ->
			getDefaultString(locale)?.let { result.add(it.copy(locale = locale, fromDefault = true)) }
		}

		return this + result
	}

	private fun List<ExcelString>.getDefaultString(locale: Locale): ExcelString? =
		find { it.locale == locale.getDefaultLocale() }

	private fun getSheet(file: File): Sheet =
		if (workbook != null && FileUtil.filesEqual(this.file, file)) {
			workbook!!.getSheetAt(0)
		} else {
			this.file = file
			workbook = WorkbookFactory.create(file)
			workbook!!.getSheetAt(0)
		}

	fun close() {
		workbook?.close()
	}
}