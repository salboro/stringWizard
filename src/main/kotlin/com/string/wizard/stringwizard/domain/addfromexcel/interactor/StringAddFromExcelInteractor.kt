package com.string.wizard.stringwizard.domain.addfromexcel.interactor

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.repository.ExcelRepository
import com.string.wizard.stringwizard.data.repository.ResourceRepository
import com.string.wizard.stringwizard.data.repository.StringRepository
import com.string.wizard.stringwizard.domain.addfromexcel.converter.StringConverter
import java.io.File

class StringAddFromExcelInteractor {

	private val excelRepository = ExcelRepository()
	private val stringRepository = StringRepository()
	private val resourceRepository = ResourceRepository()
	private val converter = StringConverter()

	fun getExcelStrings(excelFile: File, locale: Locale): List<ExcelString> =
		excelRepository.getStrings(excelFile, locale)

	fun getExcelStrings(excelFile: File, position: Int): List<ExcelString> =
		excelRepository.getStrings(excelFile, position)

	fun writeStrings(strings: List<ExcelString>, domain: Domain, newStringName: String, targetModule: Module) {
		val resourceStrings = strings.map { converter.convert(it, newStringName) }

		stringRepository.write(targetModule, domain, resourceStrings)
	}

	fun closeExcel() {
		excelRepository.close()
	}

	fun createStringFiles(module: Module, domain: Domain) {
		resourceRepository.makeStringFiles(module, domain)
	}
}