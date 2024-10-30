package com.string.wizard.stringwizard.ui.stringcopy

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextArea
import com.string.wizard.stringwizard.ui.component.ModuleListRenderer
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import org.jdesktop.swingx.VerticalLayout
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JComponent


class StringCopyDialog(project: Project) : DialogWrapper(project), StringCopyDialogUi {

    private companion object {
        const val TITLE = "Copy string"
    }

    private val presenter = StringCopyDialogPresenter(this, project)

    private val textArea = JBTextArea("Hello")
    private val dialogPanel = DialogPanel()

    private val modulesButton = JButton("Choose module", AllIcons.Actions.GroupByModule)

    init {
        title = TITLE
        init()
    }

    override fun createCenterPanel(): JComponent {
        dialogPanel.layout = VerticalLayout()

        modulesButton.addActionListener {
            presenter.onModulesChooserClick()
        }

        dialogPanel.apply {
            add(textArea)
            add(modulesButton)
        }

        return dialogPanel
    }

    override fun showModulesSelector(modules: List<Module>) {
        createSearchPopup(modules).show()
    }

    private fun createSearchPopup(modules: List<Module>): DialogWrapper =
            SearchableListDialog(
                    parent = dialogPanel,
                    label = "Search modules",
                    items = modules,
                    searchBy = { it.name },
                    itemSelectionListener = { textArea.text = it.name },
                    itemRenderer = ModuleListRenderer()
            )
}