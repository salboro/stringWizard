package com.string.wizard.stringwizard.domain.stringsort.interactor

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.repository.StringRepository

class StringSortInteractor {

	private val stringRepository = StringRepository()

	fun sort(module: Module) {
		stringRepository.sort(module)
	}
}