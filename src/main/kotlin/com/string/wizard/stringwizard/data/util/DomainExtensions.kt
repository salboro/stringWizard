package com.string.wizard.stringwizard.data.util

import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.Domain.DP
import com.string.wizard.stringwizard.data.entity.Domain.EWALLET
import com.string.wizard.stringwizard.data.entity.Domain.LOAN
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.entity.ResourcesPackage

private const val STRINGS_FILE_NAME = "strings.xml"
private const val LOAN_STRINGS_FILE_NAME = "strings_loan.xml"
private const val EWALLET_STRINGS_FILE_NAME = "strings_ewallet.xml"

fun Domain.getLocales(): List<Locale> =
	when (this) {
		DP   -> Locale.values().toList()
		LOAN -> listOf(Locale.AZ, Locale.KA, Locale.KK, Locale.KY, Locale.RO, Locale.RU, Locale.TG, Locale.UZ)
		EWALLET -> listOf(Locale.RU, Locale.KA, Locale.TG, Locale.UZ)
	}

fun Domain.getStringFileName(): String =
	when (this) {
		DP      -> STRINGS_FILE_NAME
		LOAN    -> LOAN_STRINGS_FILE_NAME
		EWALLET -> EWALLET_STRINGS_FILE_NAME
	}

fun Domain.getResourcesPackageList(): List<ResourcesPackage> =
	when (this) {
		DP      -> ResourcesPackage.values().toList()

		LOAN    -> listOf(
			ResourcesPackage.BASE,
			ResourcesPackage.AZ,
			ResourcesPackage.KA,
			ResourcesPackage.KK,
			ResourcesPackage.KY,
			ResourcesPackage.RO,
			ResourcesPackage.RU,
			ResourcesPackage.TG,
			ResourcesPackage.UZ
		)

		EWALLET -> listOf(
			ResourcesPackage.BASE,
			ResourcesPackage.KY,
			ResourcesPackage.TG,
			ResourcesPackage.UZ
		)
	}