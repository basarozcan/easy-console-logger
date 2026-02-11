package com.basarozcan.consolelogger.settings

import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import java.awt.BorderLayout
import javax.swing.JTextArea
import java.awt.Font
import javax.swing.border.EmptyBorder

class ConsoleLoggerSettingsComponent {
    private val logFormatField = JTextField().apply {
        font = Font(Font.DIALOG, Font.PLAIN, 13)
    }
    private val mainPanel: JPanel = JPanel(BorderLayout())

    init {
        // Create variables description panel
        val variablesDescription = """
            Available Variables:
            • ${'\$'}{variable} - The selected variable or expression
            • ${'\$'}{FileName} - Current file name
            • ${'\$'}{LineNumber} - Line number where console.log is inserted
            • ${'\$'}{Cursor} - Where to place the cursor after insertion
        """.trimIndent()

        val descriptionArea = JTextArea(variablesDescription).apply {
            isEditable = false
            background = null
            font = Font("JetBrains Mono", Font.PLAIN, 12)
            border = EmptyBorder(0, 0, 10, 0)
        }

        // Create input panel
        val inputPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Log format:"), logFormatField, 1, false)
            .panel

        // Add components to main panel
        mainPanel.apply {
            add(descriptionArea, BorderLayout.NORTH)
            add(inputPanel, BorderLayout.CENTER)
            border = JBUI.Borders.empty(10)
        }
    }

    fun getPanel(): JComponent = mainPanel

    fun getLogFormat(): String = logFormatField.text

    fun setLogFormat(newText: String) {
        logFormatField.text = newText
    }
}