package com.string.wizard.stringwizard.data.entity

enum class ResourcesPackage(val packageName: String) {
	BASE("values"),
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

		val packageNameList = values().map { it.packageName }
		val loanPackageNameList = listOf(AZ, KA, KK, KY, RO, RU, TG, UZ).map { it.packageName }

		fun findByPackageName(packageName: String): ResourcesPackage? =
			values().find { it.packageName == packageName }
	}
}