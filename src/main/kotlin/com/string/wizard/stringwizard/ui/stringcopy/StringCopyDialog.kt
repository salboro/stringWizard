package com.string.wizard.stringwizard.ui.stringcopy

import com.intellij.icons.AllIcons
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI.Borders
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
import javax.swing.JSeparator


class StringCopyDialog(project: Project) : DialogWrapper(project), StringCopyDialogUi {

    private companion object {

        const val TITLE = "Copy string"
        const val BORDER = 10
    }

    private val presenter = StringCopyDialogPresenter(this, project)

    private val dialogPanel = DialogPanel()

    private val sourceLabel = JBLabel("Source")
    private val targetLabel = JBLabel("Target")

    private val modulePanel = JPanel()
    private val chosenModuleLabel = JBLabel("Chosen module: ")
    private val moduleButton = JButton("", AllIcons.General.Add)

    private val stringPanel = JPanel()
    private val chosenStringLabel = JBLabel("Chosen string from resources: ")
    private val stringButton = JButton("", AllIcons.General.Add)

    private val targetModulePanel = JPanel()
    private val chosenTargetModuleLabel = JBLabel("Chosen module: ")
    private val targetModuleButton = JButton("", AllIcons.General.Add)

    init {
        title = TITLE
        init()
    }

    override fun createCenterPanel(): JComponent {
        dialogPanel.layout = VerticalLayout()

        sourceLabel.apply {
            border = Borders.empty(BORDER)
            horizontalAlignment = JBLabel.CENTER
            font = JBFont.h4()
        }

        targetLabel.apply {
            border = Borders.empty(BORDER)
            horizontalAlignment = JBLabel.CENTER
            font = JBFont.h4()
        }

        modulePanel.apply {
            layout = HorizontalLayout()
            add(chosenModuleLabel)
            add(moduleButton)
        }

        stringPanel.apply {
            layout = HorizontalLayout()
            add(chosenStringLabel)
            add(stringButton)
            border = Borders.emptyBottom(BORDER)
        }

        moduleButton.addActionListener {
            presenter.onModulesChooserClick()
        }

        stringButton.addActionListener {
            presenter.onStringSelectionClick()
        }

        targetModulePanel.apply {
            layout = HorizontalLayout()
            add(chosenTargetModuleLabel)
            add(targetModuleButton)
        }

        targetModuleButton.addActionListener {
            presenter.onTargetModuleSelectorClick()
        }

        dialogPanel.apply {
            preferredSize = Dimension(500, 300)

            add(sourceLabel)
            add(modulePanel)
            add(stringPanel)
            add(JSeparator())
            add(targetLabel)
            add(targetModulePanel)
        }

        return dialogPanel
    }

    override fun showModulesSelector(modules: List<Module>) {
        createModuleSearchDialog(modules, presenter::selectModule).show()
    }

    override fun changeSourceModuleButton(text: String, state: ButtonState) {
        moduleButton.changeModuleButton(text, state)
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

    override fun showTargetModuleSelector(modules: List<Module>) {
        createModuleSearchDialog(modules, presenter::selectTargetModule).show()
    }

    private fun createModuleSearchDialog(modules: List<Module>, selectionListener: (Module) -> Unit ): SearchableListDialog<Module> =
            SearchableListDialog(
                    parent = dialogPanel,
                    label = "Search modules",
                    items = modules,
                    searchBy = { it.name },
                    itemSelectionListener = selectionListener,
                    itemRenderer = ModuleListRenderer()
            )

    override fun changeTargetModuleButton(text: String, state: ButtonState) {
        targetModuleButton.changeModuleButton(text, state)
    }

    private fun JButton.changeModuleButton(text: String, state: ButtonState) {
        this.text = text
        icon = when (state) {
            ButtonState.EMPTY  -> AllIcons.General.Add
            ButtonState.FILLED -> AllIcons.Nodes.Module
        }
    }
}