package com.string.wizard.stringwizard.domain.stringcopy.interactor

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.data.entity.ResourcesPackage
import com.string.wizard.stringwizard.data.repository.ResourceRepository
import com.string.wizard.stringwizard.data.repository.StringRepository

class StringCopyInteractor {

	private val stringRepository = StringRepository()
	private val resourceRepository = ResourceRepository()

	fun getBaseStrings(module: Module, domain: Domain): List<ResourceString> =
		stringRepository.get(module, domain, ResourcesPackage.BASE)

	fun copyString(sourceModule: Module, targetModule: Module, sourceStringName: String, targetStringName: String, domain: Domain) {
		val sourceStrings = stringRepository.get(sourceModule, domain, sourceStringName)
		val stringsWithNewName = sourceStrings.map { it.copy(name = targetStringName) }
		stringRepository.write(targetModule, domain, stringsWithNewName)
	}

	fun createStringFiles(module: Module, domain: Domain) {
		resourceRepository.createStringFiles(module, domain)
	}
}