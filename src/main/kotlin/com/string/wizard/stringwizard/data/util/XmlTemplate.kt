package com.string.wizard.stringwizard.data.util

object XmlTemplate {

    fun resourceFileTemplate(insertContent: List<String>): String =
            """
		    |<?xml version="1.0" encoding="utf-8"?>
		    |<resources>
		    |${insertContent.joinToString(separator = "\n") { "\t$it" }}
		    |</resources>
	        """.trimMargin()

    fun stringTemplate(name: String, value: String): String =
            "<string name=\"$name\">$value</string>"
}