package com.string.wizard.stringwizard.ui.renderer

import javax.swing.table.DefaultTableCellRenderer

class UnicodeTableCellRenderer : DefaultTableCellRenderer() {

	private companion object {

		const val INVISIBLE_SYMBOLS_RANGE = 32
	}

	override fun setValue(value: Any?) {
		val convertedValue = if (value is String) {
			convertUnicodeSymbols(value)
		} else {
			value
		}

		super.setValue(convertedValue)
	}

	private fun convertUnicodeSymbols(text: String): String =
		buildString {
			append("<html>")
			text.forEach { char ->
				if (char.code < INVISIBLE_SYMBOLS_RANGE || (char.isWhitespace() && char.code != 32)) {
					append(char.getHex().addHighLight())
				} else {
					append(char)
				}
			}
			append("</html>")
		}

	private fun Char.getHex(): String =
		"\\u${code.toString(16).padStart(4, '0')}"

	private fun String.addHighLight(): String =
		"<b><font color='rgb(159, 107, 0)'>$this</font></b>"
}