package com.string.wizard.stringwizard.data.datasource

import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.entity.PluralItem
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.util.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory

class ResourceStringDataSource {

	private companion object {

		const val STRING = "string"
		const val PLURALS = "plurals"
		const val RESOURCES = "resources"
		const val NAME = "name"
		const val ITEM = "item"
		const val QUANTITY = "quantity"
	}

	private val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
	private val transformerFactory = TransformerFactory.newInstance()

	fun getDefaults(file: File, locale: Locale): List<ResourceString.Default> {
		val document = documentBuilder.parse(file)
		val stringNodes = document.getElementsByTagName(STRING)

		return stringNodes.getStringsList(locale)
	}

	fun getPlurals(file: File, locale: Locale): List<ResourceString.Plural> {
		val document = documentBuilder.parse(file)
		val pluralsNodes = document.getElementsByTagName(PLURALS)

		return pluralsNodes.getPluralsList(locale)
	}

	private fun NodeList.getStringsList(locale: Locale): List<ResourceString.Default> =
		map {
			ResourceString.Default(name = it.attributes.item(0).nodeValue, value = it.textContent, locale = locale)
		}

	private fun NodeList.getPluralsList(locale: Locale): List<ResourceString.Plural> =
		map { node ->
			val items = node.getPluralItems()
			ResourceString.Plural(node.attributes.getNamedItem(NAME).nodeValue, locale, items)
		}

	private fun Node.getPluralItems(): List<PluralItem> =
		childNodes.map { node ->
			if (node.nodeName == ITEM) {
				PluralItem(node.attributes.getNamedItem(QUANTITY).nodeValue, node.textContent)
			} else {
				null
			}
		}.filterNotNull()
}