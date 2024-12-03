package com.string.wizard.stringwizard.data.entity

enum class Locale(val packageName: String) {
	EN("values"),
	AZ("values-az"),
	DE("values-de"),
	EL("values-el"),
	ES("values-es"),
	FR("values-fr"),
	IT("values-it"),
	KA("values-ka"),
	KK("values-kk"),
	KY("values-ky"),
	RO("values-ro"),
	RU("values-ru"),
	TG("values-tg"),
	TR("values-tr"),
	UK("values-uk"),
	UZ("values-uz"),
	ZH("values-zh");

	companion object {

		fun findByPackageName(packageName: String): Locale? =
			values().find { it.packageName == packageName }

		fun packageList(): List<String> =
			values().map { it.packageName }

		val enDefaultList = listOf(EN, TR, IT, DE, EL, FR, ES)
		val ruDefaultList = listOf(RU, KK, KY, AZ, RO, TG, UZ, KA, UK, ZH)
	}
}

fun Locale.getDefaultLocale(): Locale =
	when (this) {
		in Locale.enDefaultList -> Locale.EN
		in Locale.ruDefaultList -> Locale.RU
		else                    -> error("No default locale")
	}