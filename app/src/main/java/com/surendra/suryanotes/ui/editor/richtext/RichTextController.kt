package com.surendra.suryanotes.ui.editor.richtext

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.mohamedrejeb.richeditor.model.RichTextState
import com.surendra.suryanotes.ui.editor.model.EditorHeading
import com.surendra.suryanotes.ui.editor.model.HeadingStyleMapper
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
        when (event) {
            is RichTextToolbarEvent.Action -> {
                when (event.action) {
                    ToolbarAction.TextColor ->
                        showTextColorPicker()

                    ToolbarAction.Heading -> {
                        showHeadingPicker()
                    }

                    else ->
                        processAction(event.action)
                }
            }
            is RichTextToolbarEvent.ChangeTextColor -> {
                applyTextColor(event.color)
            }

            is RichTextToolbarEvent.ChangeHeading ->{
                applyHeading(event.heading)
            }
        }
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

            ToolbarAction.TextColor -> {
                // Next phase:
                // Open ColorPickerSheet
            }

            ToolbarAction.Heading -> {

            }
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

        val currentTextColor = currentStyle.color
            .takeUnless { it == Color.Unspecified }

        val currentHeading =
            HeadingStyleMapper.toEditorHeading(
                currentStyle
            )

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

        toolbarState.value = toolbarState.value.copy(
            activeActions = activeActions,
            currentTextColor = currentTextColor,
            currentHeading = currentHeading
        )
    }

    private fun applyTextColor(
        color: Color
    ) {
        richTextState.addSpanStyle(
            SpanStyle(
                color = color
            )
        )
        refreshToolbarState()
    }

    private fun applyHeading(
        heading: EditorHeading
    ) {
        EditorHeading.entries
            .filter { it != EditorHeading.Normal }
            .forEach {
                richTextState.removeSpanStyle(
                    HeadingStyleMapper.toSpanStyle(it)
                )
            }

        if (heading != EditorHeading.Normal) {
            richTextState.toggleSpanStyle(
                HeadingStyleMapper.toSpanStyle(heading)
            )
        }
        hideHeadingPicker()

        refreshToolbarState()
    }

    fun showTextColorPicker() {
        toolbarState.value = toolbarState.value.copy(
            showTextColorPicker = true
        )
    }

    fun hideTextColorPicker() {
        toolbarState.value = toolbarState.value.copy(
            showTextColorPicker = false
        )
    }

    fun showHeadingPicker() {
        toolbarState.value = toolbarState.value.copy(
            showHeadingPicker = true
        )
    }

    fun hideHeadingPicker() {
        toolbarState.value = toolbarState.value.copy(
            showHeadingPicker = false
        )
    }
}