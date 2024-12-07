package com.string.wizard.stringwizard.ui.stringsort

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI.Borders
import com.string.wizard.stringwizard.domain.stringsort.interactor.StringSortInteractor
import com.string.wizard.stringwizard.ui.ButtonState
import com.string.wizard.stringwizard.ui.changeModuleButton
import com.string.wizard.stringwizard.ui.component.ModuleListRenderer
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_HEIGHT
import com.string.wizard.stringwizard.ui.resources.Dimension.MAIN_DIALOG_WIDTH
import org.jdesktop.swingx.HorizontalLayout
import org.jdesktop.swingx.VerticalLayout
import org.jetbrains.kotlin.idea.util.sourceRoots
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class StringSortDialog(val project: Project, dialogTitle: String) : DialogWrapper(project) {

	private companion object {

		const val BORDER = 10
	}

	private val dialogPanel = DialogPanel()

	private val sourceModulePanel = JPanel()
	private val sourceModuleLabel = JBLabel("Target module: ")
	private val sourceModuleButton = JButton("Choose Module", AllIcons.General.Add)
	private var targetModule: Module? = null

	init {
		title = dialogTitle
		init()
	}

	override fun createCenterPanel(): JComponent {
		dialogPanel.layout = VerticalLayout()
		val modules = project.modules.filter { module ->
			module.sourceRoots.any { !it.path.contains("test", ignoreCase = true) }
		}

		sourceModuleButton.addActionListener {
			SearchableListDialog(
				parent = dialogPanel,
				label = "Search modules",
				items = modules,
				searchBy = { it.name },
				itemSelectionListener = { module ->
					targetModule = module
					sourceModuleButton.changeModuleButton(module.name, ButtonState.FILLED)
				},
				itemRenderer = ModuleListRenderer()
			).show()
		}

		sourceModuleLabel.apply {
			border = Borders.empty(BORDER)
			horizontalAlignment = JBLabel.HORIZONTAL
			font = JBFont.h4()
		}

		sourceModulePanel.apply {
			layout = HorizontalLayout()
			add(sourceModuleLabel)
			add(sourceModuleButton)
		}

		dialogPanel.apply {
			preferredSize = Dimension(MAIN_DIALOG_WIDTH, MAIN_DIALOG_HEIGHT)
			add(sourceModulePanel)
		}

		return dialogPanel
	}

	override fun doOKAction() {
		if (targetModule != null) {
			val interactor = StringSortInteractor()
			try {
				targetModule?.let(interactor::sort)
				super.doOKAction()
			} catch (e: Exception) {
				// stay tuned
			}
		}
	}
}