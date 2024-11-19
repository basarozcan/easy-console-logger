package com.example.consolelogger.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class ConsoleLoggerSettingsConfigurable : Configurable {
    private var settingsComponent: ConsoleLoggerSettingsComponent? = null

    override fun getDisplayName(): String = "Console Logger Settings"

    override fun createComponent(): JComponent {
        settingsComponent = ConsoleLoggerSettingsComponent()
        settingsComponent?.setLogFormat(ConsoleLoggerSettings.getInstance().logFormat)
        return settingsComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        val settings = ConsoleLoggerSettings.getInstance()
        return settingsComponent?.getLogFormat() != settings.logFormat
    }

    override fun apply() {
        val settings = ConsoleLoggerSettings.getInstance()
        settings.logFormat = settingsComponent?.getLogFormat() ?: return
    }

    override fun reset() {
        val settings = ConsoleLoggerSettings.getInstance()
        settingsComponent?.setLogFormat(settings.logFormat)
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}