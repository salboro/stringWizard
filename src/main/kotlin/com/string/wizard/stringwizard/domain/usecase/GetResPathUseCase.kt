package com.string.wizard.stringwizard.domain.usecase

class GetResPathUseCase {

	private companion object {

		const val RES_DIRECTORY_PATH = "/src/main/res/"
	}

	operator fun invoke(): String = RES_DIRECTORY_PATH
}