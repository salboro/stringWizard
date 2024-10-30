package com.string.wizard.stringwizard.ui.stringcopy

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.string.wizard.stringwizard.ui.component.ModuleListRenderer
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import org.jdesktop.swingx.VerticalLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel


class StringCopyDialog(project: Project) : DialogWrapper(project), StringCopyDialogUi {

    private companion object {
        const val TITLE = "Copy string"
    }

    private val presenter = StringCopyDialogPresenter(this, project)

    private val textArea = JBTextArea("Hello")
    private val dialogPanel = DialogPanel()

    private val modulePanel = JPanel()
    private val chosenModuleLabel = JBLabel("Chosen module: ")
    private val moduleButton = JButton("", AllIcons.General.Inline_edit)

    init {
        title = TITLE
        init()
    }

    override fun createCenterPanel(): JComponent {
        dialogPanel.layout = VerticalLayout()

        modulePanel.apply {
            add(chosenModuleLabel)
            add(moduleButton)
        }

        moduleButton.addActionListener {
            presenter.onModulesChooserClick()
        }

        dialogPanel.apply {
            add(textArea)
            add(modulePanel)
        }

        return dialogPanel
    }

    override fun showModulesSelector(modules: List<Module>) {
        createSearchPopup(modules).show()
    }

    private fun createSearchPopup(modules: List<Module>): SearchableListDialog<Module> =
            SearchableListDialog(
                    parent = dialogPanel,
                    label = "Search modules",
                    items = modules,
                    searchBy = { it.name },
                    itemSelectionListener = presenter::selectModule,
                    itemRenderer = ModuleListRenderer()
            )

    override fun changeModuleButton(text: String, state: ButtonState) {
        moduleButton.text = text
        moduleButton.icon = when (state) {
            ButtonState.EMPTY  -> AllIcons.General.Add
            ButtonState.FILLED -> AllIcons.Nodes.Module
        }
    }
}