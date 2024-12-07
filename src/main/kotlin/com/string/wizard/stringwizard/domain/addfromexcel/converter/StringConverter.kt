package com.string.wizard.stringwizard.domain.addfromexcel.converter

import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.data.entity.ResourceString

class StringConverter {

	fun convert(from: ExcelString, name: String): ResourceString =
		ResourceString(name, from.value, from.locale)
}