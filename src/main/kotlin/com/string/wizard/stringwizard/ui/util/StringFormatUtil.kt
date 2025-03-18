package com.string.wizard.stringwizard.ui.util

import com.string.wizard.stringwizard.data.entity.Locale

fun formatResourceString(name: String, value: String, locale: Locale): String =
	"<html><b><font color=#7EDC95>name:</b></font> $name       <font color=#7EDC95><b>value:</b></font> $value       <font color=#7EDC95><b>locale:</b></font> ${locale.name}</html>"

fun copySuccessMessageFormat(sourceModuleName: String, targetModuleName: String, sourceStringName: String, newStringName: String): String =
	"""
		|<html>
		|<br>String successfully copied! 
		|<br>Info:
		|<br>	Source string: <b><font color=#7EDC95>$sourceStringName</b></font>
		|<br>	Source module: <b><font color=#7EDC95>$sourceModuleName</b></font>
		|<br>	Target module: <b><font color=#7EDC95>$targetModuleName</b></font>
		|<br>	New string name: <b><font color=#7EDC95>$newStringName</b></font>
		|<html>
	    """.trimMargin()

fun formatExcelString(value: String, position: Int, locale: Locale): String =
	"<html><b><font color=#7EDC95>value:</b></font> $value       <font color=#7EDC95><b>position:</b></font> $position       <font color=#7EDC95><b>locale:</b></font> ${locale.name}</html>"

fun formatModuleName(name: String): String =
	name.removePrefix("qpay-rarog-android.").removeSuffix(".main")