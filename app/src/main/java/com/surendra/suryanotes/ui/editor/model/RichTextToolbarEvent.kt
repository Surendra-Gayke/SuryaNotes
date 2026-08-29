package com.surendra.suryanotes.ui.editor.model

import androidx.compose.ui.graphics.Color

sealed interface RichTextToolbarEvent {

    data class Action(
        val action: ToolbarAction
    ) : RichTextToolbarEvent

    data class ChangeTextColor(
        val color: Color
    ) : RichTextToolbarEvent
}