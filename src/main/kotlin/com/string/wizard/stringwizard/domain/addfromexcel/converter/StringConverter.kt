package com.string.wizard.stringwizard.domain.addfromexcel.converter

import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.domain.entity.NewString

class StringConverter {

	fun convert(from: ExcelString, name: String): ResourceString.Default =
		ResourceString.Default(name, from.locale, from.value)

	fun convertFromNew(from: NewString, name: String): ResourceString.Default =
		ResourceString.Default(name, from.locale, from.value)
}