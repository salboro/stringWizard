package com.string.wizard.stringwizard.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.string.wizard.stringwizard.ui.stringsort.StringSortDialog


class StringSortAction : DumbAwareAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(event: AnActionEvent) {
        StringSortDialog(event.project, templatePresentation.text).show()
    }
}