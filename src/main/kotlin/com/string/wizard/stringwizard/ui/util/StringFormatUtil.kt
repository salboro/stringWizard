package com.string.wizard.stringwizard.ui.util

import com.string.wizard.stringwizard.data.entity.Locale

fun formatResourceString(name: String, value: String, locale: Locale) =
        "<html><b><font color=#7EDC95>name:</b></font> $name       <font color=#7EDC95><b>value:</b></font> $value       <font color=#7EDC95><b>locale:</b></font> ${locale.name}</html>"