package com.string.wizard.stringwizard.ui.util

import com.string.wizard.stringwizard.data.entity.Locale

fun formatResourceString(name: String, value: String, locale: Locale) =
        "name: $name       value: $value       locale: ${locale.name}"