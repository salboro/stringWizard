package com.string.wizard.stringwizard.data.entity

sealed interface ResourceString {

	val name: String
	val locale: Locale

	data class Default(
		override val name: String,
		override val locale: Locale,
		val value: String,
	) : ResourceString

	data class Plural(
		override val name: String,
		override val locale: Locale,
		val items: List<PluralItem>,
	) : ResourceString
}

data class PluralItem(val quantity: String, val value: String)