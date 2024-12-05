package com.string.wizard.stringwizard.data.util

import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.Domain.DP
import com.string.wizard.stringwizard.data.entity.Domain.LOAN
import com.string.wizard.stringwizard.data.entity.Locale

private const val STRINGS_FILE_NAME = "strings.xml"
private const val LOAN_STRINGS_FILE_NAME = "strings_loan.xml"

fun Domain.getLocales(): List<Locale> =
	when (this) {
		DP   -> Locale.values().toList()
		LOAN -> listOf(Locale.AZ, Locale.KA, Locale.KK, Locale.KY, Locale.RO, Locale.RU, Locale.TG, Locale.UZ)
	}

fun Domain.getStringFileName(): String =
	when (this) {
		DP   -> STRINGS_FILE_NAME
		LOAN -> LOAN_STRINGS_FILE_NAME
	}