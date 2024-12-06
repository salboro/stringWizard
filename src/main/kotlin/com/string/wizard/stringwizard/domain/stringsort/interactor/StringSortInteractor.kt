package com.string.wizard.stringwizard.domain.stringsort.interactor

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.repository.NewStringRepository1

class StringSortInteractor {

	private val stringRepository = NewStringRepository1()

	fun sort(module: Module) {
		stringRepository.sort(module)
	}
}