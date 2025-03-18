package com.string.wizard.stringwizard.data.datasource

import com.android.utils.forEach
import com.string.wizard.stringwizard.data.entity.Locale
import com.string.wizard.stringwizard.data.entity.PluralItem
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.entity.ResourcesPackage
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class ResourceStringDataSource {

	fun getDefaults(file: File, locale: Locale?): List<ResourceString.Default> {
		val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
		val xPath = XPathFactory.newInstance().newXPath()
		val strings: NodeList = xPath.evaluate(".//string", builder, XPathConstants.NODESET) as NodeList

		return strings.getStringsList(locale)
	}

	fun getPlurals(file: File, locale: Locale?): List<ResourceString.Plural> {
		val builder = getDocumentBuilder(file)
		val xPath = XPathFactory.newInstance().newXPath()
		val plurals: NodeList = xPath.evaluate(".//plurals", builder, XPathConstants.NODESET) as NodeList

		return plurals.getPluralsList(xPath, locale)
	}

	private fun getDocumentBuilder(file: File) =
		DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

	private fun NodeList.getStringsList(locale: Locale?): List<ResourceString.Default> =
		buildList {
			this@getStringsList.forEach {
				add(ResourceString.Default(name = it.attributes.item(0).nodeValue, value = it.textContent, locale = locale))
			}
		}

	private fun NodeList.getPluralsList(xPath: XPath, locale: Locale?): List<ResourceString.Plural> =
		buildList {
			this@getPluralsList.forEach {
				val itemsExpression = xPath.compile(".//item")
				val itemsNodeList = itemsExpression.evaluate(it, XPathConstants.NODESET) as NodeList
				val listOfItems = mutableListOf<PluralItem>()
				itemsNodeList.forEach { node ->
					listOfItems.add(PluralItem(quantity = node.attributes.getNamedItem("quantity").nodeValue, value = node.textContent))
				}
				add(ResourceString.Plural(it.attributes.getNamedItem("name").nodeValue, locale = locale, listOfItems))
			}
		}
}