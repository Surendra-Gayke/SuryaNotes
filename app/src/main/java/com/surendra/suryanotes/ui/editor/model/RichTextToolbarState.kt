package com.surendra.suryanotes.ui.editor.model

import androidx.compose.ui.graphics.Color

data class RichTextToolbarState(
    val activeActions: Set<ToolbarAction> = emptySet(),
    val currentTextColor: Color? = null,
    val showTextColorPicker: Boolean = false
)