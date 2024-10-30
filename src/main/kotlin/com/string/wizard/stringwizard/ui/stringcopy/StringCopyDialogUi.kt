package com.string.wizard.stringwizard.ui.stringcopy

import com.intellij.openapi.module.Module

interface StringCopyDialogUi {

    fun showModulesSelector(modules: List<Module>)
}