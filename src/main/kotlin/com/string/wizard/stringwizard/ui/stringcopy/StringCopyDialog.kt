package com.string.wizard.stringwizard.ui.stringcopy

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.string.wizard.stringwizard.data.entity.ResourceString
import com.string.wizard.stringwizard.ui.component.ModuleListRenderer
import com.string.wizard.stringwizard.ui.component.SearchableListDialog
import com.string.wizard.stringwizard.ui.component.StringListRenderer
import org.jdesktop.swingx.HorizontalLayout
import org.jdesktop.swingx.VerticalLayout
import java.awt.Dimension
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
    private val moduleButton = JButton("", AllIcons.General.Add)

    private val stringPanel = JPanel()
    private val chosenStringLabel = JBLabel("Chosen string from resources: ")
    private val stringButton = JButton("", AllIcons.General.Add)

    init {
        title = TITLE
        init()
    }

    override fun createCenterPanel(): JComponent {
        dialogPanel.layout = VerticalLayout()

        modulePanel.apply {
            layout = HorizontalLayout()
            add(chosenModuleLabel)
            add(moduleButton)
        }

        stringPanel.apply {
            layout = HorizontalLayout()
            add(chosenStringLabel)
            add(stringButton)
        }

        moduleButton.addActionListener {
            presenter.onModulesChooserClick()
        }

        stringButton.addActionListener {
            presenter.onStringSelectionClick()
        }

        dialogPanel.apply {
            preferredSize = Dimension(500, 300)

            add(textArea)
            add(modulePanel)
            add(stringPanel)
        }

        return dialogPanel
    }

    override fun showModulesSelector(modules: List<Module>) {
        createModuleSearchDialog(modules).show()
    }

    private fun createModuleSearchDialog(modules: List<Module>): SearchableListDialog<Module> =
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

    override fun showStringSelector(strings: List<ResourceString>) {
        createStringSelectorDialog(strings).show()
    }

    private fun createStringSelectorDialog(strings: List<ResourceString>): SearchableListDialog<ResourceString> =
            SearchableListDialog(
                    parent = dialogPanel,
                    label = "Select string from resources",
                    items = strings,
                    searchBy = { it.name },
                    itemSelectionListener = presenter::selectString,
                    itemRenderer = StringListRenderer(),
            )

    override fun changeStringButton(text: String, state: ButtonState) {
        stringButton.text = text
        stringButton.icon = when (state) {
            ButtonState.EMPTY  -> AllIcons.General.Add
            ButtonState.FILLED -> AllIcons.FileTypes.Xml
        }
    }
}