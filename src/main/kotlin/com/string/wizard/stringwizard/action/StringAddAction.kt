package com.string.wizard.stringwizard.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.string.wizard.stringwizard.ui.stringadd.StringAddDialog

class StringAddAction : DumbAwareAction() {

	override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

	override fun actionPerformed(event: AnActionEvent) {
		val currentProject = event.project ?: error("No project founded")
		StringAddDialog(currentProject, templatePresentation.text).show()
	}
}