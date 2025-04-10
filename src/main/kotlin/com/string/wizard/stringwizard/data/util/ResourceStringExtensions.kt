package com.string.wizard.stringwizard.data.util

import com.string.wizard.stringwizard.data.entity.ResourceString

fun ResourceString.Plural.getFirstValue(): String =
	with(items.first()) {
		"$quantity: $value"
	}