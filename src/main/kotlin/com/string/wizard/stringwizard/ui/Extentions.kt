package com.string.wizard.stringwizard.ui

import com.intellij.icons.AllIcons
import javax.swing.JButton

fun JButton.changeModuleButton(text: String, state: ButtonState) {
    this.text = text
    icon = when (state) {
        ButtonState.EMPTY -> AllIcons.General.Add
        ButtonState.FILLED -> AllIcons.Nodes.Module
    }
}