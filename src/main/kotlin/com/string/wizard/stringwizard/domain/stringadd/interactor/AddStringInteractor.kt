package com.string.wizard.stringwizard.domain.stringadd.interactor

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.repository.ResourceRepository
import com.string.wizard.stringwizard.data.repository.StringRepository
import com.string.wizard.stringwizard.domain.addfromexcel.converter.StringConverter
import com.string.wizard.stringwizard.domain.entity.NewString

class AddStringInteractor {

	private val resourceRepository = ResourceRepository()
	private val stringRepository = StringRepository()
	private val converter = StringConverter()

	fun createFiles(module: Module, domain: Domain) {
		resourceRepository.createStringFiles(module, domain)
	}

	fun writeStrings(targetModule: Module, domain: Domain, strings: List<NewString>, newStringName: String) {
		val resStrings = strings.map { converter.convertFromNew(it, newStringName) }
		stringRepository.write(targetModule, domain, resStrings)
	}
}