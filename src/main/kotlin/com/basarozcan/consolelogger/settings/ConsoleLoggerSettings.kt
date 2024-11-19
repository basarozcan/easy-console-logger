package com.basarozcan.consolelogger.settings

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "ConsoleLoggerSettings",
    storages = [Storage("ConsoleLoggerSettings.xml")]
)
class ConsoleLoggerSettings : PersistentStateComponent<ConsoleLoggerSettings> {
    var logFormat: String = "console.log('[${'\$'}{FileName}:${'\$'}{LineNumber}] ${'\$'}{variable}${'\$'}{Cursor}');"

    override fun getState(): ConsoleLoggerSettings = this

    override fun loadState(state: ConsoleLoggerSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): ConsoleLoggerSettings = service()
    }
}