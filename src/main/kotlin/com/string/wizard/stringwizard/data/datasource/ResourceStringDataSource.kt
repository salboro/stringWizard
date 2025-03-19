package com.string.wizard.stringwizard.data.datasource

import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.entity.PluralItem
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.util.map
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class ResourceStringDataSource {

	private companion object {

		const val STRING = "string"
		const val PLURALS = "plurals"
		const val RESOURCES = "resources"
		const val NAME = "name"
		const val ITEM = "item"
		const val QUANTITY = "quantity"

		const val YES = "yes"
		const val NO = "no"

		const val UTF_8 = "utf-8"

		const val INDENT_AMOUNT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount"
		const val INDENT_AMOUNT = 1

		const val STANDALONE_ATTRIBUTE = " standalone=\"no\""
	}

	private val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
	private val transformerFactory = TransformerFactory.newInstance()

	private val startSpacesRegex = Regex("^\\s+", RegexOption.MULTILINE)

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

	fun write(file: File, strings: List<ResourceString>) {
		val xmlSource = createXmlSource(strings)
		val transformer = createTransformer()
		val writer = StringWriter()

		transformer.transform(xmlSource, StreamResult(writer))
		file.writeText(getValidFormatString(writer))
	}

	private fun createXmlSource(strings: List<ResourceString>): DOMSource {
		val document = documentBuilder.newDocument()
		val resources = document.createElement(RESOURCES)
		val (defaults, plurals) = strings.filterIsInstance<ResourceString.Default>() to strings.filterIsInstance<ResourceString.Plural>()

		defaults.forEach { string ->
			val stringElement = document.createElement(STRING)
			stringElement.setAttribute(NAME, string.name)
			stringElement.textContent = string.value
			resources.appendChild(stringElement)
		}

		plurals.forEach { string ->
			val pluralsElement = document.createElement(PLURALS)
			pluralsElement.setAttribute(NAME, string.name)
			string.items.forEach { item ->
				val itemElement = document.createElement(ITEM)
				itemElement.setAttribute(QUANTITY, item.quantity)
				itemElement.textContent = item.value
				pluralsElement.appendChild(itemElement)
			}
			resources.appendChild(pluralsElement)
		}

		document.appendChild(resources)

		return DOMSource(document)
	}

	private fun createTransformer(): Transformer =
		transformerFactory.newTransformer().apply {
			setOutputProperty(OutputKeys.INDENT, YES)
			setOutputProperty(OutputKeys.ENCODING, UTF_8)
			setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, NO)
			setOutputProperty(INDENT_AMOUNT_PROPERTY, INDENT_AMOUNT.toString())
		}

	/**
	 * Метод необходим, так как
	 * 		1. Transformer за счет включенного OutputKeys.INDENT выставляет отступы. Но делать он это умеет только пробелами, поэтому тут замена на табы
	 * 		2. Transformer добавляет аттрибут standalone, который не получается убрать настройками при создании объекта. Поэтому удаляем вручную
	 * 		3. Transformer добавляет лишний перенос строки в конце файла, удалить его выходить только вручную
	 */
	private fun getValidFormatString(writer: StringWriter): String =
		writer.buffer.replace(startSpacesRegex) { "\t".repeat(it.value.length) }
			.replace(STANDALONE_ATTRIBUTE, "")
			.trimEnd('\n')
}