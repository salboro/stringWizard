package com.string.wizard.stringwizard.ui

import com.android.tools.idea.projectsystem.isMainModule
import com.android.tools.idea.projectsystem.isUnitTestModule
import com.intellij.collaboration.ui.CollaborationToolsUIUtil.defaultButton
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBPanel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.panel
import com.string.wizard.stringwizard.domain.usecase.GetResPathUseCase
import com.string.wizard.stringwizard.ui.resources.Dimension.BASE_HEIGHT
import com.string.wizard.stringwizard.ui.resources.Dimension.BASE_WIDTH
import com.string.wizard.stringwizard.ui.resources.Strings
import com.string.wizard.stringwizard.ui.resources.Strings.STRINGS_SORT_CHOOSE
import org.jetbrains.kotlin.idea.base.projectStructure.externalProjectPath
import org.jetbrains.kotlin.idea.base.util.isAndroidModule
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JComponent

class StringSortDialog(val project: Project?, dialogTitle: String) : DialogWrapper(
    project,
    null,
    true,
    IdeModalityType.MODELESS,
    false
) {

    private companion object {

        const val DEFAULT_EXIT_CODE = -1
    }


    private val state = StringSortState()
    lateinit var panel: DialogPanel

    init {
        title = dialogTitle
        init()
    }

    override fun createCenterPanel(): JComponent {
        val result = createDialogPanelHolder()
        val modules = project?.modules?.toList()
            ?.filter { it.isAndroidModule() && it.isMainModule() && !it.isUnitTestModule() }
            ?.sortedBy { it.name }
            ?: emptyList()

        state.module = if (modules.isNotEmpty()) modules.first() else null

        panel = getDialogPanel(modules)

        result.add(panel)

        return result
    }

    private fun createDialogPanelHolder(): JBPanel<DialogPanel> {
        val result = JBPanel<DialogPanel>(BorderLayout())
        result.minimumSize = Dimension(BASE_WIDTH, BASE_HEIGHT)
        result.maximumSize = Dimension(BASE_WIDTH, BASE_HEIGHT)
        return result
    }

    private fun getDialogPanel(modules: List<Module>): DialogPanel = panel {
        val positiveButton = JButton(Strings.POSITIVE_BUTTON_TEXT)
        positiveButton.addActionListener { positiveActionEvent() }
        positiveButton.defaultButton()

        row {
            comboBox(modules).align(AlignX.FILL).onChanged {
                state.module = it.item
            }.label(STRINGS_SORT_CHOOSE, LabelPosition.TOP)
        }.bottomGap(BottomGap.MEDIUM)

        row {
            button(Strings.NEGATIVE_BUTTON_TEXT) { close(DEFAULT_EXIT_CODE) }
            cell(positiveButton)
        }
    }

    private fun positiveActionEvent() {
        val module = state::module.get()
        if (module != null) {
            val moduleStringsSorter = ModuleStringResSorter(GetResPathUseCase())
            try {
                module.externalProjectPath?.let { strings -> moduleStringsSorter.sort(strings) }
                close(DEFAULT_EXIT_CODE)
            } catch (e: Exception) {
                // stay tuned
            }
        }
    }
}