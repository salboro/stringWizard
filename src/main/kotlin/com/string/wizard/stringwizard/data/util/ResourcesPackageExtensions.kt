package com.string.wizard.stringwizard.data.util

import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.entity.ResourcesPackage
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.AZ
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.BASE
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.DE
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.EL
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.ES
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.FR
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.IT
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.KA
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.KK
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.KY
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.RO
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.RU
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.TG
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.TR
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.UK
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.UZ
import com.string.wizard.stringwizard.data.entity.ResourcesPackage.ZH

fun ResourcesPackage.getLocale(): Locale =
	when (this) {
		BASE -> Locale.EN
		AZ   -> Locale.AZ
		DE   -> Locale.DE
		EL   -> Locale.EL
		ES   -> Locale.ES
		FR   -> Locale.FR
		IT   -> Locale.IT
		KA   -> Locale.KA
		KK   -> Locale.KK
		KY   -> Locale.KY
		RO   -> Locale.RO
		RU   -> Locale.RU
		TG   -> Locale.TG
		TR   -> Locale.TR
		UK   -> Locale.UK
		UZ   -> Locale.UZ
		ZH   -> Locale.ZH
	}