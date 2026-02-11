package com.basarozcan.consolelogger.settings

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.Converter
import java.util.Base64

class UnicodeStringConverter : Converter<String>() {
    override fun toString(value: String): String {
        return Base64.getEncoder().encodeToString(value.toByteArray(Charsets.UTF_8))
    }

    override fun fromString(value: String): String {
        return try {
            String(Base64.getDecoder().decode(value), Charsets.UTF_8)
        } catch (_: IllegalArgumentException) {
            // Fallback for old plain-text settings before the converter was added
            value
        }
    }
}

@State(
    name = "ConsoleLoggerSettings",
    storages = [Storage("ConsoleLoggerSettings.xml")]
)
class ConsoleLoggerSettings : PersistentStateComponent<ConsoleLoggerSettings> {
    @OptionTag(converter = UnicodeStringConverter::class)
    var logFormat: String = "console.log('[${'\$'}{FileName}:${'\$'}{LineNumber}] ${'\$'}{variable}${'\$'}{Cursor}');"

    override fun getState(): ConsoleLoggerSettings = this

    override fun loadState(state: ConsoleLoggerSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): ConsoleLoggerSettings = service()
    }
}