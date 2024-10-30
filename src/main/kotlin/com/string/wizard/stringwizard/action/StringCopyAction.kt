package com.string.wizard.stringwizard.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.string.wizard.stringwizard.ui.stringcopy.StringCopyDialog


class StringCopyAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val currentProject = event.project ?: error("No project")
        StringCopyDialog(currentProject).show()
    }
}