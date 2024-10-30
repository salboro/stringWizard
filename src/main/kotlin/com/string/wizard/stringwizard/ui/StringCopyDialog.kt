package com.string.wizard.stringwizard.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextArea
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
    private val textAreaEmpty = JBTextArea("Empty modules")
    private val dialogPanel = DialogPanel()
    private val button = JButton("Click!")

    private val listModel = DefaultListModel<String>()
    private val list = JBList(listModel)

    private val moduleSearch = SearchTextField()
    private val modulesButton = JButton("Choose module", AllIcons.Actions.GroupByModule)
    private val modulesPopup = JBPopupMenu("Choose module")

    init {
        title = TITLE
        init()
    }

    override fun createCenterPanel(): JComponent {
        dialogPanel.layout = VerticalLayout()

        button.addActionListener {
            presenter.onClick()
        }

        textAreaEmpty.isVisible = false

        modulesPopup.add(moduleSearch)
        modulesPopup.add(list)

        modulesButton.addActionListener {
            presenter.onModulesChooserClick()
        }

        dialogPanel.apply {
            add(textArea)
            add(textAreaEmpty)
            add(modulesButton)
            add(button)
        }

        presenter.onCreate()
        return dialogPanel
    }

    override fun showModulesSelector(modules: List<Module>) {
//        createSearchPopup(List(300) { modules.first() }).show()
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