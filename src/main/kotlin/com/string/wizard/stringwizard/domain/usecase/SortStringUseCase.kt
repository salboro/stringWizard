package com.string.wizard.stringwizard.domain.usecase

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.repository.StringRepository

class SortStringUseCase {

	private val stringRepository = StringRepository()

	fun sort(module: Module) {
		stringRepository.sort(module)
	}
}