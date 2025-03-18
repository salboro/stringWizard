package com.string.wizard.stringwizard.data.entity

enum class Locale {
	EN,
	AZ,
	DE,
	EL,
	ES,
	FR,
	IT,
	KA,
	KK,
	KY,
	RO,
	RU,
	TG,
	TR,
	UK,
	UZ,
	ZH;

	companion object {

		val enDefaultList = listOf(EN, TR, IT, DE, EL, FR, ES)
		val ruDefaultList = listOf(RU, KK, KY, AZ, RO, TG, UZ, KA, UK, ZH)
	}
}