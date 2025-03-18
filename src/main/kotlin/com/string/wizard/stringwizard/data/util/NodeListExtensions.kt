package com.string.wizard.stringwizard.data.util

import com.android.utils.forEach
import org.w3c.dom.Node
import org.w3c.dom.NodeList

fun <T> NodeList.map(transform: (Node) -> T): List<T> =
	buildList {
		this@map.forEach { node ->
			add(transform(node))
		}
	}