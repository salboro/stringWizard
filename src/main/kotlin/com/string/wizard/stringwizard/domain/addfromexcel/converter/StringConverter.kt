package com.string.wizard.stringwizard.domain.addfromexcel.converter

import com.string.wizard.stringwizard.data.entity.ExcelString
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.domain.entity.NewString

// TODO: Перенести куда-то в общее место, так как используется не только для добавление из excel.
//  Или же после переноса NewString вынести часть функционала в отдельный конвертер
class StringConverter {

	fun convert(from: ExcelString, name: String): ResourceString.Default =
		ResourceString.Default(name, from.locale, from.value)

	fun convertFromNew(from: NewString, name: String): ResourceString.Default =
		ResourceString.Default(name, from.locale, from.value)
}