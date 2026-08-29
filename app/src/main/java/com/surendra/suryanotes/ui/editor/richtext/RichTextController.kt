package com.surendra.suryanotes.ui.editor.richtext

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.mohamedrejeb.richeditor.model.RichTextState
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarEvent
import com.surendra.suryanotes.ui.editor.model.ToolbarAction
import kotlinx.coroutines.flow.MutableStateFlow
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarState
class RichTextController(

    private val richTextState: RichTextState,
    private val toolbarState: MutableStateFlow<RichTextToolbarState>

) {

    fun onToolbarEvent(
        event: RichTextToolbarEvent
    ) {
        processAction(event.action)
    }

    fun onSelectionChanged() {
        refreshToolbarState()
    }

    private fun updateEditor(
        action: ToolbarAction
    ) {

        when (action) {

            ToolbarAction.Bold -> richTextState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))

            ToolbarAction.Italic -> richTextState.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))

            ToolbarAction.Underline -> richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))

            ToolbarAction.Strike -> richTextState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
        }

    }

    private fun processAction(
        action: ToolbarAction
    ) {
        updateEditor(action)
        refreshToolbarState()
    }


    private fun refreshToolbarState() {

        val currentStyle = richTextState.currentSpanStyle

        val activeActions = buildSet {

            if (currentStyle.fontWeight == FontWeight.Bold) {
                add(ToolbarAction.Bold)
            }

            if (currentStyle.fontStyle == FontStyle.Italic) {
                add(ToolbarAction.Italic)
            }

            if (
                currentStyle.textDecoration?.contains(
                    TextDecoration.Underline
                ) == true
            ) {
                add(ToolbarAction.Underline)
            }

            if (
                currentStyle.textDecoration?.contains(
                    TextDecoration.LineThrough
                ) == true
            ) {
                add(ToolbarAction.Strike)
            }
        }

        toolbarState.value = RichTextToolbarState(
            activeActions = activeActions
        )
    }
}