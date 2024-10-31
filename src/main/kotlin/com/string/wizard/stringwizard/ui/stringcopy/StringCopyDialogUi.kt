package com.string.wizard.stringwizard.ui.stringcopy

import com.intellij.openapi.module.Module
import com.string.wizard.stringwizard.data.entity.ResourceString

interface StringCopyDialogUi {

    fun showModulesSelector(modules: List<Module>)

    fun showTargetModuleSelector(modules: List<Module>)

    fun changeSourceModuleButton(text: String, state: ButtonState)

    fun changeTargetModuleButton(text: String, state: ButtonState)

    fun showStringSelector(strings: List<ResourceString>)

    fun changeStringButton(text: String, state: ButtonState)
}