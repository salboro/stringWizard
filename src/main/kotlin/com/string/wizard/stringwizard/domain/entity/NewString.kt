package com.string.wizard.stringwizard.domain.entity

import com.string.wizard.stringwizard.data.entity.Locale

/*
	TODO: Поле position нигде не используется.

	Подумать о переносе этой сущности на presentation слой.
	Кажется, что её существование обусловлено структурой ui, поэтому она где-то там и должна быть
 */
data class NewString(val value: String, val position: Int, val locale: Locale)