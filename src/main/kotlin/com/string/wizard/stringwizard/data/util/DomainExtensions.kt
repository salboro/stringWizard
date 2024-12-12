package com.string.wizard.stringwizard.data.util

import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.Domain.DP
import com.string.wizard.stringwizard.data.entity.Domain.EWALLET
import com.string.wizard.stringwizard.data.entity.Domain.LOAN
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.entity.Locale.AZ
import com.string.wizard.stringwizard.data.entity.Locale.DE
import com.string.wizard.stringwizard.data.entity.Locale.EL
import com.string.wizard.stringwizard.data.entity.Locale.EN
import com.string.wizard.stringwizard.data.entity.Locale.ES
import com.string.wizard.stringwizard.data.entity.Locale.FR
import com.string.wizard.stringwizard.data.entity.Locale.IT
import com.string.wizard.stringwizard.data.entity.Locale.KA
import com.string.wizard.stringwizard.data.entity.Locale.KK
import com.string.wizard.stringwizard.data.entity.Locale.KY
import com.string.wizard.stringwizard.data.entity.Locale.RO
import com.string.wizard.stringwizard.data.entity.Locale.RU
import com.string.wizard.stringwizard.data.entity.Locale.TG
import com.string.wizard.stringwizard.data.entity.Locale.TR
import com.string.wizard.stringwizard.data.entity.Locale.UK
import com.string.wizard.stringwizard.data.entity.Locale.UZ
import com.string.wizard.stringwizard.data.entity.Locale.ZH
import com.string.wizard.stringwizard.data.entity.ResourcesPackage

private const val STRINGS_FILE_NAME = "strings.xml"
private const val LOAN_STRINGS_FILE_NAME = "strings_loan.xml"
private const val EWALLET_STRINGS_FILE_NAME = "strings_ewallet.xml"

fun Domain.getLocales(): List<Locale> =
	when (this) {
		DP      -> listOf(EN, RU, AZ, KA, KK, KY, RO, TG, UK, UZ, ZH, DE, EL, ES, FR, IT, TR)
		LOAN    -> listOf(RU, AZ, KA, KK, KY, RO, TG, UZ)
		EWALLET -> listOf(RU, KY, TG, UZ)
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