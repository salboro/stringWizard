package com.string.wizard.stringwizard.data.datasource

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.entity.ResourcesPackage
import com.string.wizard.stringwizard.data.exception.InvalidModulePathException
import com.string.wizard.stringwizard.data.exception.NotEnoughStringFilesException
import com.string.wizard.stringwizard.data.util.DirectoryPath
import com.string.wizard.stringwizard.data.util.getResourcesPackageList
import com.string.wizard.stringwizard.data.util.getStringFileName
import org.jetbrains.kotlin.idea.base.projectStructure.externalProjectPath
import java.io.File

class FileDataSource {

	/**
	 * Метод для получения ресурсных директорий
	 *
	 * @param module модуль, в котором происходит поиск директорий
	 * @param domain домен, в соответствии с которым будет происходить поиск. Если его не указать, будут выданы все ресурсные директории с values в названии
	 * @throws NotEnoughStringFilesException если в модуле отсутствуют value директории или их недостаточно для указанного домена
	 */
	fun getResourceDirectories(module: Module, domain: Domain? = null): List<File> {
		val path = module.externalProjectPath ?: throw InvalidModulePathException("Invalid module path: ${module.externalProjectPath}")
		val resDirectoryPath = path + DirectoryPath.RES_DIRECTORY
		val validDirectoriesNames = domain?.getResourcesPackageList()?.map(ResourcesPackage::packageName)
		val directories = File(resDirectoryPath)
			.walk()
			.filter { it.isValidDirectory(validDirectoriesNames) }
			.toList()

		assertDirectories(directories, module, domain)
		return directories
	}

	private fun File.isValidDirectory(validNames: List<String>?): Boolean =
		isDirectory && validNames?.let { name in it } ?: name.contains("values")

	private fun assertDirectories(directories: List<File>, module: Module, domain: Domain? = null) {
		when {
			domain != null && domain.getResourcesPackageList().size > directories.size -> {
				throw NotEnoughStringFilesException("Not enough resources packages for domain ${domain.name}")
			}

			directories.isEmpty() -> {
				throw NotEnoughStringFilesException("No values directories in module ${module.externalProjectPath}")
			}
		}
	}

	/**
	 * Метод для получения файла со строками
	 *
	 * @param directory директория, в которой будет происходить поиск файла
	 * @param domain домен, в соответствии с которым будет происходить поиск. В зависимости от домена строковый файл имеет разные названия
	 * @throws NotEnoughStringFilesException если в директории нет подходящего строкового файла
	 */
	fun getStringFile(directory: File, domain: Domain): File {
		val fileName = domain.getStringFileName()

		return directory
			.walk()
			.find { it.isValidFile(fileName) }
			?: throw NotEnoughStringFilesException("No $fileName file in directory ${directory.absolutePath}")
	}

	private fun File.isValidFile(validName: String): Boolean =
		isFile && name == validName
}