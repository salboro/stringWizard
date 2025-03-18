package com.string.wizard.stringwizard.data.util

import com.string.wizard.stringwizard.data.entity.ResourceString

object XmlTemplate {

	fun resourceFileTemplate(insertContent: List<String>): String =
		"""
		    |<?xml version="1.0" encoding="utf-8"?>
		    |<resources>
		    |${insertContent.joinToString(separator = "\n") { "\t$it" }}
		    |</resources>
	        """.trimMargin()

	fun resourceFileTemplateDefault(insertDefaults: List<ResourceString.Default>, insertPlurals: List<ResourceString.Plural>): String =
		if (insertPlurals.isEmpty()) {
			"""
		    |<?xml version="1.0" encoding="utf-8"?>
		    |<resources>
		    |${insertDefaults.joinToString(separator = "\n") { "\t<string name=\"${it.name}\">${it.value}</string>" }}
		    |</resources>
	        """.trimMargin()
		} else {
			"""
		    |<?xml version="1.0" encoding="utf-8"?>
		    |<resources>
		    |${insertDefaults.joinToString(separator = "\n") { "\t<string name=\"${it.name}\">${it.value}</string>" }}
			|
			|${insertPlurals.joinToString(separator = "\n\n") { pluralTemplate(it) }}
		    |</resources>
	        """.trimMargin()
		}

	fun pluralTemplate(value: ResourceString.Plural): String {
		val quantityiesString = value.items.map { "\t\t<item quantity=\"${it.quantity}\">${it.value}</item>" }.joinToString("\n")

		return "\t<plurals name=\"${value.name}\">\n" + quantityiesString + "\n\t</plurals>"
	}
}