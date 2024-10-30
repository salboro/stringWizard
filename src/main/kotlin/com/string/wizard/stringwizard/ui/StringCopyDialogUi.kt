package com.string.wizard.stringwizard.ui

import com.intellij.openapi.module.Module

interface StringCopyDialogUi {

    fun showModulesSelector(modules: List<Module>)
}