package com.string.wizard.stringwizard.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.string.wizard.stringwizard.ui.Icons
import com.string.wizard.stringwizard.ui.StringCopyDialog


class StringCopyAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {

        // Using the event, create and show a dialog
        val currentProject = event.project ?: error("No project")
        var message = "${event.presentation.text} Selected!"

        // If an element is selected in the editor, add info about it.
        val selectedElement = event.getData(CommonDataKeys.NAVIGATABLE)
        if (selectedElement != null) {
            message += "\nSelected Element: $selectedElement"
        }
        val title = event.presentation.description
       StringCopyDialog(currentProject).show()
    }
}