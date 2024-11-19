package com.basarozcan.consolelogger

import com.basarozcan.consolelogger.settings.ConsoleLoggerSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.fileEditor.FileDocumentManager

class AddConsoleLogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val project: Project = e.getRequiredData(CommonDataKeys.PROJECT)
        val document: Document = editor.document
        
        val selectedText = editor.selectionModel.selectedText
        val primaryCaret = editor.caretModel.primaryCaret
        val lineNumber = document.getLineNumber(primaryCaret.offset) + 1
        
        val virtualFile = FileDocumentManager.getInstance().getFile(document)
        val fileName = virtualFile?.name ?: "unknown"
        
        var format = ConsoleLoggerSettings.getInstance().logFormat
        val hasCursorPlaceholder = format.contains("\${Cursor}")
        
        // Calculate cursor position relative to the start of the log statement
        val cursorRelativePosition = if (hasCursorPlaceholder) {
            val beforeCursor = format.substringBefore("\${Cursor}")
                .replace("\${variable}", selectedText ?: "")
                .replace("\${FileName}", fileName)
                .replace("\${LineNumber}", lineNumber.toString())
            beforeCursor.length
        } else {
            -1 // Indicates no cursor placeholder
        }
        
        val logStatement = if (selectedText != null && selectedText.isNotEmpty()) {
            format.replace("\${Cursor}", "")
                 .replace("\${variable}", selectedText)
                 .replace("\${FileName}", fileName)
                 .replace("\${LineNumber}", lineNumber.toString())
        } else {
            format.replace("\${Cursor}", "")
                 .replace("\${variable}", "")
                 .replace("\${FileName}", fileName)
                 .replace("\${LineNumber}", lineNumber.toString())
                 .replace("''", "``")
                 .replace("\"\"", "``")
                 .replace(":,", ":")
                 .replace("  ", " ")
        }
        
        // Get the current line's indentation level
        val currentLineStartOffset = document.getLineStartOffset(document.getLineNumber(primaryCaret.offset))
        val currentLineEndOffset = document.getLineEndOffset(document.getLineNumber(primaryCaret.offset))
        val currentLine = document.getText().substring(currentLineStartOffset, currentLineEndOffset)
        val indentation = currentLine.takeWhile { it.isWhitespace() }
        
        // Get the next line's indentation level (if it exists)
        val currentLineNumber = document.getLineNumber(primaryCaret.offset)
        val nextLineIndentation = if (currentLineNumber < document.lineCount - 1) {
            val nextLineStart = document.getLineStartOffset(currentLineNumber + 1)
            val nextLineEnd = document.getLineEndOffset(currentLineNumber + 1)
            val nextLine = document.getText().substring(nextLineStart, nextLineEnd)
            nextLine.takeWhile { it.isWhitespace() }
        } else {
            indentation
        }
        
        // Use the larger indentation level
        val finalIndentation = if (nextLineIndentation.length > indentation.length) {
            nextLineIndentation
        } else {
            indentation
        }
        
        WriteCommandAction.runWriteCommandAction(project) {
            // Insert the log statement with proper indentation
            document.insertString(currentLineEndOffset, "\n${finalIndentation}${logStatement}")
            
            // Calculate the final cursor position
            val newLineStart = currentLineEndOffset + 1 + finalIndentation.length
            val newOffset = if (hasCursorPlaceholder) {
                newLineStart + cursorRelativePosition
            } else {
                newLineStart + logStatement.length // End of the inserted line
            }
            
            // Clear selection and move cursor
            editor.selectionModel.removeSelection()
            editor.caretModel.moveToOffset(newOffset)
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = project != null && editor != null
    }
}