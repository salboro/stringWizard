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

		fun isEnLocale(string: String): Boolean = string in listOf(
			EN.packageName,
			DE.packageName,
			EL.packageName,
			ES.packageName,
			FR.packageName,
			IT.packageName,
			TR.packageName,
		)
	}
}