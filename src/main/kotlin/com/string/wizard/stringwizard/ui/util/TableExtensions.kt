package com.string.wizard.stringwizard.ui.util

import com.intellij.ui.table.JBTable


private const val MINIMAL_COLUMN_WIDTH = 50
private const val ADDITIONAL_INDENT = 10

fun JBTable.adjustColumnWidths() {
	for (columnIndex in 0 until columnModel.columnCount) {
		var maxWidth = MINIMAL_COLUMN_WIDTH // Минимальная ширина столбца
		for (rowIndex in 0 until rowCount) {
			val renderer = getCellRenderer(rowIndex, columnIndex)
			val component = renderer.getTableCellRendererComponent(
				this, getValueAt(rowIndex, columnIndex),
				false, false, rowIndex, columnIndex
			)
			maxWidth = maxOf(maxWidth, component.preferredSize.width)
		}
		// Учитываем ширину заголовка столбца
		val headerRenderer = tableHeader.defaultRenderer
		val headerComponent = headerRenderer.getTableCellRendererComponent(
			this, columnModel.getColumn(columnIndex).headerValue,
			false, false, -1, columnIndex
		)
		maxWidth = maxOf(maxWidth, headerComponent.preferredSize.width)

		// Устанавливаем рассчитанную ширину
		columnModel.getColumn(columnIndex).preferredWidth = maxWidth + ADDITIONAL_INDENT
	}
}