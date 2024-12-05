package com.string.wizard.stringwizard.data.util

import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.entity.Locale.AZ
import com.string.wizard.stringwizard.data.entity.Locale.Companion.enDefaultList
import com.string.wizard.stringwizard.data.entity.Locale.Companion.ruDefaultList
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

fun Locale.getDefaultLocale(): Locale =
	when (this) {
		in enDefaultList -> EN
		in ruDefaultList -> RU
		else             -> error("No default locale")
	}

fun Locale.getPackage(domain: Domain = Domain.DP): ResourcesPackage =
	when (domain) {
		Domain.DP   -> getDpPackage()
		Domain.LOAN -> getLoanPackage()
	}

private fun Locale.getDpPackage(): ResourcesPackage =
	when (this) {
		EN -> ResourcesPackage.BASE
		AZ -> ResourcesPackage.AZ
		DE -> ResourcesPackage.DE
		EL -> ResourcesPackage.EL
		ES -> ResourcesPackage.ES
		FR -> ResourcesPackage.FR
		IT -> ResourcesPackage.IT
		KA -> ResourcesPackage.KA
		KK -> ResourcesPackage.KK
		KY -> ResourcesPackage.KY
		RO -> ResourcesPackage.RO
		RU -> ResourcesPackage.RU
		TG -> ResourcesPackage.TG
		TR -> ResourcesPackage.TR
		UK -> ResourcesPackage.UK
		UZ -> ResourcesPackage.UZ
		ZH -> ResourcesPackage.ZH
	}

private fun Locale.getLoanPackage(): ResourcesPackage =
	when (this) {
		RU   -> ResourcesPackage.BASE
		AZ   -> ResourcesPackage.AZ
		KA   -> ResourcesPackage.KA
		KK   -> ResourcesPackage.KK
		KY   -> ResourcesPackage.KY
		RO   -> ResourcesPackage.RO
		TG   -> ResourcesPackage.TG
		UZ   -> ResourcesPackage.UZ
		else -> error("Invalid locale for loans $name")
	}