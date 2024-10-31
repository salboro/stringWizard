package com.string.wizard.stringwizard.ui.component

import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI.Borders
import org.jdesktop.swingx.VerticalLayout
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class SearchableListDialog<T>(
        parent: Component,
        label: String,
        private val items: List<T>,
        private val size: Dimension = Dimension(700, 500),
        private val searchBy: (T) -> String = { it.toString() },
        private val itemSelectionListener: (T) -> Unit = { },
        private val itemRenderer: ListCellRenderer<T>? = null,
) : DialogWrapper(parent, false) {

    private companion object {

        const val BORDER = 10
        const val DOUBLE = 2
    }

    private val mainPanel = DialogPanel(BorderLayout())

    private val popupLabel = JBLabel(label)
    private val listModel = DefaultListModel<T>()
    private val list = JBList(listModel)
    private val topPanel = JPanel(VerticalLayout())

    private val search = SearchTextField()

    private val searchDocumentListener = object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) {
            changedUpdate(e)
        }

        override fun removeUpdate(e: DocumentEvent?) {
            changedUpdate(e)
        }

        override fun changedUpdate(e: DocumentEvent?) {
            filterItems(search.text)
        }
    }

    private val doubleClickListener: MouseListener = object : MouseAdapter() {
        override fun mouseClicked(mouseEvent: MouseEvent?) {
            val list = mouseEvent?.source as? JBList<*> ?: return
            if (mouseEvent.clickCount == DOUBLE) {
                val index = list.locationToIndex(mouseEvent.getPoint());
                if (index >= 0) {
                    val item = listModel.get(index)
                    itemSelectionListener(item)
                    close(OK_EXIT_CODE, true)
                }
            }
        }
    }

    init {
        init()
    }

    override fun createCenterPanel(): JComponent {
        list.apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            border = Borders.empty(BORDER)
            addMouseListener(doubleClickListener)
            itemRenderer?.let { this.cellRenderer = it }
        }

        popupLabel.apply {
            verticalAlignment = JBLabel.CENTER
            horizontalAlignment = JBLabel.CENTER
            border = Borders.empty(0, BORDER, BORDER, BORDER)
            font = JBFont.h4()
        }

        search.apply {
            border = Borders.emptyBottom(BORDER)
            addDocumentListener(searchDocumentListener)
        }

        listModel.addAll(items)

        topPanel.apply {
            add(popupLabel)
            add(search)
        }

        mainPanel.apply {
            preferredSize = this@SearchableListDialog.size

            add(topPanel, BorderLayout.NORTH)
            add(JBScrollPane(list), BorderLayout.CENTER)
        }

        return mainPanel
    }

    private fun filterItems(query: String) {
        if (query.isNotBlank()) {
            val filteredItems = items.filter { searchBy(it).contains(query) }
            listModel.removeAllElements()
            listModel.addAll(filteredItems)
        } else {
            listModel.removeAllElements()
            listModel.addAll(items)
        }
    }

    override fun doOKAction() {
        list.selectedValue?.let { itemSelectionListener(it) }

        super.doOKAction()
    }
}