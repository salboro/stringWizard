package com.string.wizard.stringwizard.data.repository

import com.intellij.openapi.module.Module
import com.intellij.util.io.createDirectories
import com.string.wizard.stringwizard.data.entity.Domain
import com.string.wizard.stringwizard.data.util.DirectoryPath
import com.string.wizard.stringwizard.data.util.XmlTemplate
import com.string.wizard.stringwizard.data.util.getResourcesPackageList
import com.string.wizard.stringwizard.data.util.getStringFileName
import org.jetbrains.kotlin.idea.base.projectStructure.externalProjectPath
import java.io.File

class ResourceRepository {

	/**
	 * Метод для создания строковых файлов.
	 * Название файлов и пакеты, где они будут созданы, будут соответствовать домену.
	 *
	 * @param module модуль в котором будут создаваться строковые файлы
	 * @param domain домен, в соответствии с которым будут создаваться файлы
	 */
	fun createStringFiles(module: Module, domain: Domain) {
		val packageNames = domain.getResourcesPackageList().map { it.packageName }
		val resPath = module.externalProjectPath + DirectoryPath.RES_DIRECTORY
		val stringFileName = domain.getStringFileName()

		packageNames.forEach { valueDirectoryName ->
			val file = File("$resPath/$valueDirectoryName/$stringFileName")
			file.parentFile.toPath().createDirectories()
			file.writeText(XmlTemplate.resourceFileTemplate(emptyList()))
		}
	}
}